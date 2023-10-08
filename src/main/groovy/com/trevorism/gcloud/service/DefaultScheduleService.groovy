package com.trevorism.gcloud.service

import com.google.cloud.tasks.v2beta3.*
import com.google.gson.Gson
import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import com.trevorism.bean.CorrelationIdProvider
import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.data.model.filtering.FilterBuilder
import com.trevorism.data.model.filtering.SimpleFilter
import com.trevorism.gcloud.schedule.model.InternalTokenRequest
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.ClasspathBasedPropertiesProvider
import com.trevorism.PropertiesProvider
import com.trevorism.https.SecureHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@jakarta.inject.Singleton
class DefaultScheduleService implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger( DefaultScheduleService )

    private Repository<ScheduledTask> repository
    private ScheduledTaskValidator validator = new ScheduledTaskValidator(this)
    private CorrelationIdProvider provider
    private SecureHttpClient secureHttpClient

    DefaultScheduleService(SecureHttpClient secureHttpClient, CorrelationIdProvider provider){
        this.secureHttpClient = secureHttpClient;
        repository = new PingingDatastoreRepository<>(ScheduledTask, secureHttpClient)
        this.provider = provider
    }

    @Override
    ScheduledTask create(ScheduledTask schedule) {
        schedule = ScheduledTaskValidator.cleanup(schedule)
        validator.validate(schedule, false)
        schedule = repository.create(schedule)
        enqueue(schedule)
        return schedule
    }

    @Override
    ScheduledTask getByName(String name) {
        def task = repository.filter(new FilterBuilder().addFilter(new SimpleFilter("name", "=", name.toLowerCase())).build())
        if (!task)
            return null
        return task[0]
    }

    @Override
    ScheduledTask update(ScheduledTask schedule, String name) {
        schedule = ScheduledTaskValidator.cleanup(schedule)
        validator.validate(schedule, true)

        ScheduledTask existingTask = getByName(name)
        schedule = repository.update(existingTask.id, schedule)

        enqueue(schedule)
        return schedule
    }

    @Override
    List<ScheduledTask> list() {
        repository.list()
    }

    @Override
    boolean delete(String name) {
        ScheduledTask task = getByName(name)
        if (task) {
            repository.delete(task.id)
        }
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
        def list = repository.all()
        list.each { ScheduledTask st ->
            enqueue(st)
        }
        return list
    }

    @Override
    boolean cleanup() {
        def date = Date.from(ZonedDateTime.now().minusDays(1).toInstant())
        def list = repository.list()
        def immediates = list.findAll{ it.type == "immediate" && it.startDate < date}
        log.info("Number of old schedules to delete: ${immediates.size()}")
        immediates.each {
            delete(it.name)
        }

        return immediates
    }

    void enqueueSchedule(ScheduledTask schedule, ScheduleType scheduleType) {
        long nowMillis = Instant.now().toEpochMilli()
        long scheduleSeconds = (nowMillis + scheduleType.getCountdownMillis(schedule)) / 1000

        String correlationId = UUID.randomUUID().toString()
        log.info("Enqueuing schedule ${schedule.name} using correlationId: ${correlationId}")
        log.info("Scheduled for ${scheduleSeconds - (nowMillis/1000)} seconds from now using correlationId: ${correlationId}")

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
            Date twoHoursFromNow = Date.from(Instant.now().plus(2, ChronoUnit.HOURS))
            PropertiesProvider pp = new ClasspathBasedPropertiesProvider()
            String subject = pp.getProperty("clientId")
            Gson gson = new Gson()
            InternalTokenRequest tokenRequest = new InternalTokenRequest(expiration: twoHoursFromNow, subject: subject, tenantId: scheduledTask.tenantId)
            return secureHttpClient.post("https://auth.trevorism.com/token/internal", gson.toJson(tokenRequest))
        } catch (Exception ignored) {
            log.warn("Unable to get token; new schedules will not be authenticated.")
        }
    }
}
