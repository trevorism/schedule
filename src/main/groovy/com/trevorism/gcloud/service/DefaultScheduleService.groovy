package com.trevorism.gcloud.service

import com.google.cloud.tasks.v2beta3.*
import com.google.gson.Gson
import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import com.trevorism.ClasspathBasedPropertiesProvider
import com.trevorism.PropertiesProvider
import com.trevorism.bean.CorrelationIdProvider
import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.InternalTokenRequest
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.https.AppClientSecureHttpClient
import com.trevorism.https.SecureHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.time.Instant
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@jakarta.inject.Singleton
class DefaultScheduleService implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DefaultScheduleService)

    private Repository<ScheduledTask> repository
    private ScheduledTaskValidator validator = new ScheduledTaskValidator(this)
    private CorrelationIdProvider provider
    private SecureHttpClient secureHttpClient

    DefaultScheduleService(CorrelationIdProvider provider) {
        this.secureHttpClient = new AppClientSecureHttpClient()
        this.repository = new PingingDatastoreRepository<>(ScheduledTask, secureHttpClient)
        this.provider = provider
    }

    @Override
    ScheduledTask create(ScheduledTask schedule, String tenantId) {
        schedule = ScheduledTaskValidator.cleanup(schedule, tenantId)
        validator.validate(schedule)
        schedule = repository.create(schedule)
        enqueue(schedule)
        return schedule
    }

    @Override
    ScheduledTask get(String id) {
        repository.get(id)
    }

    @Override
    ScheduledTask update(String id, ScheduledTask schedule) {
        schedule = ScheduledTaskValidator.cleanup(schedule, null)
        validator.validate(schedule)

        ScheduledTask existingTask = get(id)
        schedule.tenantId = existingTask.tenantId
        schedule = repository.update(existingTask.id, schedule)

        enqueue(schedule)
        return schedule
    }

    @Override
    List<ScheduledTask> list() {
        repository.list()
    }

    @Override
    ScheduledTask delete(String id) {
        ScheduledTask task = get(id)
        if (task) {
            repository.delete(task.id)
        }
        return task
    }

    @Override
    void enqueue(ScheduledTask schedule) {
        ScheduleType type = ScheduleTypeFactory.create(schedule.type)
        if (shouldBeEnqueued(schedule, type)) {
            enqueueSchedule(schedule, type)
        }
    }

    @Override
    boolean enqueueAll() {
        def list = repository.list()
        list.each { ScheduledTask st ->
            enqueue(st)
        }
        return list
    }

    @Override
    boolean cleanup() {
        def date = Date.from(ZonedDateTime.now().minusDays(1).toInstant())
        def list = repository.list()
        def immediates = list.findAll { it.type == "immediate" && it.startDate < date }
        log.info("Number of old schedules to delete: ${immediates.size()}")
        immediates.each {
            delete(it.id)
        }

        return immediates
    }

    void enqueueSchedule(ScheduledTask schedule, ScheduleType scheduleType) {
        long nowMillis = Instant.now().toEpochMilli()
        long scheduleSeconds = (nowMillis + scheduleType.getCountdownMillis(schedule)) / 1000

        String correlationId = UUID.randomUUID().toString()
        log.info("Enqueuing schedule ${schedule.name} using correlationId: ${correlationId}")
        log.info("Scheduled for ${scheduleSeconds - (nowMillis / 1000)} seconds from now using correlationId: ${correlationId}")

        HttpRequest httpRequest = constructHttpRequest(schedule)

        Task.Builder taskBuilder = Task.newBuilder()
                .setScheduleTime(Timestamp.newBuilder().setSeconds(scheduleSeconds).build())
                .setHttpRequest(httpRequest)
        final CloudTasksClient client = CloudTasksClient.create()
        final String queuePath = QueueName.of("trevorism-action", "us-east4", "default").toString()
        Task task = client.createTask(queuePath, taskBuilder.build())

        if (task.name && scheduleType.name == "immediate") {
            schedule.enabled = false
            log.info("${correlationId}: Updating schedule to enabled=false since the schedule is already enqueued. This avoids accidental multiple invocations.")
            repository.update(schedule.id, schedule)
        }

        client.shutdown()
        client.awaitTermination(3, TimeUnit.SECONDS)
    }

    private HttpRequest constructHttpRequest(ScheduledTask schedule) {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder().setUrl(schedule.endpoint).putHeaders("Content-Type", "application/json")
        String scheduleToken = getScheduleToken(schedule)
        if (scheduleToken) {
            httpBuilder = httpBuilder.putHeaders(SecureHttpClient.AUTHORIZATION, SecureHttpClient.BEARER_ + scheduleToken).putHeaders(CorrelationIdProvider.X_CORRELATION_ID, provider.getCorrelationId())
        }
        if (schedule.httpMethod == "get") {
            httpBuilder = httpBuilder.setHttpMethod(HttpMethod.GET)
        } else if (schedule.httpMethod == "post") {
            httpBuilder = httpBuilder.setBody(ByteString.copyFrom(schedule.requestJson, Charset.defaultCharset())).setHttpMethod(HttpMethod.POST)
        } else if (schedule.httpMethod == "put") {
            httpBuilder = httpBuilder.setBody(ByteString.copyFrom(schedule.requestJson, Charset.defaultCharset())).setHttpMethod(HttpMethod.PUT)
        } else if (schedule.httpMethod == "patch") {
            httpBuilder = httpBuilder.setBody(ByteString.copyFrom(schedule.requestJson, Charset.defaultCharset())).setHttpMethod(HttpMethod.PATCH)
        } else if (schedule.httpMethod == "delete") {
            httpBuilder = httpBuilder.setHttpMethod(HttpMethod.DELETE)
        }
        httpBuilder.build()
    }

    private static boolean shouldBeEnqueued(ScheduledTask scheduledTask, ScheduleType scheduleType) {
        long nowMillis = Instant.now().toEpochMilli()
        long startDateMillis = scheduledTask.startDate.getTime()
        long scheduleTimeMillis = scheduleType.getCountdownMillis(scheduledTask)

        if (scheduleTimeMillis == ScheduleType.WILL_NEVER_ENQUEUE) {
            return false
        }
        if (nowMillis + ScheduleType.HOURS_IN_MILLISECONDS < startDateMillis) {
            return false
        }
        if (scheduleTimeMillis > ScheduleType.HOURS_IN_MILLISECONDS) {
            return false
        }
        return scheduledTask.enabled
    }

    private String getScheduleToken(ScheduledTask scheduledTask) {
        try {
            PropertiesProvider pp = new ClasspathBasedPropertiesProvider()
            String subject = pp.getProperty("clientId")
            InternalTokenRequest tokenRequest = new InternalTokenRequest(subject: subject, tenantId: scheduledTask.tenantId)
            return secureHttpClient.post("https://auth.trevorism.com/token/internal", new Gson().toJson(tokenRequest))
        } catch (Exception ignored) {
            log.warn("Unable to get token; new schedules will not be authenticated.")
        }
        return null
    }
}
