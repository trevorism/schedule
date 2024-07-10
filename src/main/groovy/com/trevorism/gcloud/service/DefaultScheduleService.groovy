package com.trevorism.gcloud.service

import com.google.cloud.tasks.v2beta3.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import com.trevorism.PropertiesProvider
import com.trevorism.data.FastDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.DatastoreTokenRequest
import com.trevorism.gcloud.schedule.model.InternalTokenRequest
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.https.SecureHttpClient
import com.trevorism.https.SecureHttpClientBase
import com.trevorism.https.token.ObtainTokenFromAuthServiceFromPropertiesFile
import com.trevorism.https.token.ObtainTokenFromParameter
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.time.Instant
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@jakarta.inject.Singleton
class DefaultScheduleService implements ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DefaultScheduleService)

    private Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
    private HttpClient singletonClient = new JsonHttpClient()

    @Inject
    private SecureHttpClient secureHttpClient
    @Inject
    PropertiesProvider propertiesProvider

    @Override
    ScheduledTask create(ScheduledTask schedule, String tenantId) {
        schedule = ScheduledTaskValidator.cleanup(schedule, tenantId)
        ScheduledTaskValidator.validate(schedule)
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, secureHttpClient)
        schedule = repository.create(schedule)
        enqueue(schedule, repository)
        return schedule
    }

    @Override
    ScheduledTask get(String id) {
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, secureHttpClient)
        repository.get(id)
    }

    @Override
    ScheduledTask update(String id, ScheduledTask schedule) {
        schedule = ScheduledTaskValidator.cleanup(schedule, null)
        ScheduledTaskValidator.validate(schedule)

        ScheduledTask existingTask = get(id)
        schedule.tenantId = existingTask.tenantId
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, secureHttpClient)
        schedule = repository.update(existingTask.id, schedule)

        enqueue(schedule, repository)
        return schedule
    }

    @Override
    List<ScheduledTask> list() {
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, secureHttpClient)
        repository.list()
    }

    @Override
    ScheduledTask delete(String id) {
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, secureHttpClient)
        ScheduledTask task = get(id)
        if (task) {
            repository.delete(task.id)
        }
        return task
    }

    @Override
    void enqueue(ScheduledTask schedule, Repository<ScheduledTask> repository) {
        ScheduleType type = ScheduleTypeFactory.create(schedule.type)
        if (shouldBeEnqueued(schedule, type)) {
            enqueueSchedule(schedule, type, repository)
        }
    }

    @Override
    boolean enqueueAll() {
        List<ScheduledTask> list = getScheduledTasksAcrossAllTenants()
        list.each { ScheduledTask st ->
            enqueue(st, null)
        }
        return list
    }

    @Override
    boolean cleanup() {
        List<ScheduledTask> list = getScheduledTasksAcrossAllTenants()
        def date = Date.from(ZonedDateTime.now().minusDays(1).toInstant())
        def immediates = list.findAll { it.type == "immediate" && it.startDate < date }
        log.info("Number of old schedules to delete: ${immediates.size()}")
        immediates.each {
            delete(it.id)
        }
        return immediates
    }

    private void enqueueSchedule(ScheduledTask schedule, ScheduleType scheduleType, Repository<ScheduledTask> repository) {
        long nowMillis = Instant.now().toEpochMilli()
        long scheduleSeconds = (long)((nowMillis + scheduleType.getCountdownMillis(schedule)) / 1000)

        log.info("Enqueuing schedule ${schedule.name} for ${scheduleSeconds - (nowMillis / 1000)} seconds from now")
        HttpRequest httpRequest = constructHttpRequest(schedule)

        Task.Builder taskBuilder = Task.newBuilder()
                .setScheduleTime(Timestamp.newBuilder().setSeconds(scheduleSeconds).build())
                .setHttpRequest(httpRequest)
        final CloudTasksClient client = CloudTasksClient.create()
        final String queuePath = QueueName.of("trevorism-action", "us-east4", "default").toString()
        Task task = client.createTask(queuePath, taskBuilder.build())

        if (task.name && scheduleType.name == "immediate" && repository != null) {
            schedule.enabled = false
            log.debug("Updating schedule.enabled to false since the schedule is already enqueued. This avoids accidental multiple invocations.")
            repository.update(schedule.id, schedule)
        }

        client.shutdown()
        client.awaitTermination(3, TimeUnit.SECONDS)
    }

    private HttpRequest constructHttpRequest(ScheduledTask schedule) {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder().setUrl(schedule.endpoint).putHeaders("Content-Type", "application/json")
        String scheduleToken = getScheduleToken(schedule)
        if (scheduleToken) {
            httpBuilder = httpBuilder.putHeaders(SecureHttpClient.AUTHORIZATION, SecureHttpClient.BEARER_ + scheduleToken)
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
            String subject = propertiesProvider.getProperty("clientId")
            InternalTokenRequest tokenRequest = new InternalTokenRequest(subject: subject, tenantId: scheduledTask.tenantId)
            SecureHttpClient appHttpClient = new SecureHttpClientBase(singletonClient, new ObtainTokenFromAuthServiceFromPropertiesFile()) {}
            return appHttpClient.post("https://auth.trevorism.com/token/internal", new Gson().toJson(tokenRequest))
        } catch (Exception ignored) {
            log.warn("Unable to get token; new schedules will not be authenticated.")
        }
        return null
    }

    private List<ScheduledTask> getScheduledTasksAcrossAllTenants() {
        String token = getTokenForSystemRoleToDatastoreAudience()
        SecureHttpClient httpClientForSystemRoleToDatastoreAudience = new SecureHttpClientBase(singletonClient,
                new ObtainTokenFromParameter(token)) {}
        Repository<ScheduledTask> repository = new FastDatastoreRepository<>(ScheduledTask, httpClientForSystemRoleToDatastoreAudience)
        return repository.all()
    }

    private String getTokenForSystemRoleToDatastoreAudience() {
        try {
            DatastoreTokenRequest tokenRequest = DatastoreTokenRequest.get(propertiesProvider)
            return secureHttpClient.post("https://auth.trevorism.com/token", gson.toJson(tokenRequest))
        } catch (Exception e) {
            log.error("Unable to get token", e)
            throw e
        }
    }
}
