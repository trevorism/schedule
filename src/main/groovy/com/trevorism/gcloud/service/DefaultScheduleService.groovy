package com.trevorism.gcloud.service

import com.google.cloud.tasks.v2beta3.*
import com.google.protobuf.ByteString
import com.google.protobuf.Timestamp
import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.data.model.filtering.FilterBuilder
import com.trevorism.data.model.filtering.SimpleFilter
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.secure.PropertiesProvider

import java.nio.charset.Charset
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 * @author tbrooks
 */
class DefaultScheduleService implements ScheduleService {

    private static final Logger log = Logger.getLogger(DefaultScheduleService.class.name)

    private Repository<ScheduledTask> repository = new PingingDatastoreRepository<>(ScheduledTask)
    private ScheduledTaskValidator validator = new ScheduledTaskValidator(this)
    private String scheduleToken

    DefaultScheduleService() {
        try {
            PropertiesProvider pp = new PropertiesProvider()
            scheduleToken = pp.getProperty("scheduleToken")
        } catch (Exception ignored) {
            log.warning("Unable to get scheduleToken; new schedules will not be authenticated.")
        }
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
        def list = repository.list()
        list.each { ScheduledTask st ->
            enqueue(st)
        }
        return list
    }

    void enqueueSchedule(ScheduledTask schedule, ScheduleType scheduleType) {
        long nowMillis = Instant.now().toEpochMilli()
        long scheduleSeconds = (nowMillis + scheduleType.getCountdownMillis(schedule)) / 1000

        HttpRequest httpRequest = constructHttpRequest(schedule)

        Task.Builder taskBuilder = Task.newBuilder()
                .setScheduleTime(Timestamp.newBuilder().setSeconds(scheduleSeconds).build())
                .setHttpRequest(httpRequest)
        final CloudTasksClient client = CloudTasksClient.create()
        final String queuePath = QueueName.of("trevorism-gcloud", "us-east1", "default").toString()
        Task task = client.createTask(queuePath, taskBuilder.build())

        if (task.name && scheduleType.name == "immediate") {
            schedule.enabled = false
            repository.update(schedule.id, schedule)
        }

        client.shutdown()
        client.awaitTermination(2, TimeUnit.SECONDS)
    }

    private HttpRequest constructHttpRequest(ScheduledTask schedule) {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder().setUrl(schedule.endpoint).putHeaders("Content-Type", "application/json")
        if (scheduleToken) {
            httpBuilder = httpBuilder.putHeaders("Authorization", "bearer " + scheduleToken)
        }
        if (schedule.httpMethod == "get") {
            httpBuilder = httpBuilder.setHttpMethod(HttpMethod.GET)
        } else if (schedule.httpMethod == "post") {
            httpBuilder = httpBuilder.setBody(ByteString.copyFrom(schedule.requestJson, Charset.defaultCharset())).setHttpMethod(HttpMethod.POST)
        } else if (schedule.httpMethod == "put") {
            httpBuilder = httpBuilder.setBody(ByteString.copyFrom(schedule.requestJson, Charset.defaultCharset())).setHttpMethod(HttpMethod.PUT)
        } else if (schedule.httpMethod == "delete") {
            httpBuilder = httpBuilder.setHttpMethod(HttpMethod.DELETE)
        }
        httpBuilder.build()
    }

    private static boolean shouldBeEnqueued(ScheduledTask scheduledTask, ScheduleType scheduleType) {
        long nowMillis = Instant.now().toEpochMilli()
        long startDateMillis = scheduledTask.startDate.getTime()
        long scheduleTimeMillis = scheduleType.getCountdownMillis(scheduledTask)
        long oneHourFromNow = 1000 * 60 * 60

        if (scheduleTimeMillis == ScheduleType.WILL_NEVER_ENQUEUE) {
            return false
        }
        if (nowMillis + oneHourFromNow < startDateMillis) {
            return false
        }
        if (scheduleTimeMillis > oneHourFromNow) {
            return false
        }
        return scheduledTask.enabled
    }
}
