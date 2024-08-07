package com.trevorism.gcloud.service

import com.trevorism.PropertiesProvider
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * @author tbrooks
 */
class DefaultScheduleServiceTest {


    DefaultScheduleService scheduleService
    private int addedToQueue = 0

    @BeforeEach
    void setup() {
        scheduleService = new DefaultScheduleService()
        scheduleService.singletonClient = new TestSecureHttpClient()
        scheduleService.secureHttpClient = new TestSecureHttpClient()
        scheduleService.propertiesProvider = [getProperty: { String s -> return s }] as PropertiesProvider

        addedToQueue = 0
        scheduleService.create(TestScheduleService.createTestScheduledTaskNow(), null)
    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(id: "456", name: "test2", type: "minute", startDate: new Date(), enabled: false, endpoint: "endpoint", httpMethod: "get")
        ScheduledTask createdTask = scheduleService.create(task, null)
        assert scheduleService.list().size() == 2
        assert createdTask.id
    }

    @Test
    void testGet() {
        ScheduledTask task = scheduleService.get("123")
        assert task.name == "test"
        assert task.id == "123"
        assert !task.enabled
    }

    @Test
    void testList() {
        assert scheduleService.list()
        assert scheduleService.list().size() == 1
    }

    @Test
    void testDelete() {
        assert scheduleService.delete("123", null)
    }

    @Test
    void testUpdate() {
        ScheduledTask task = new ScheduledTask(id: "123", name: "test", type: "hourly", startDate: new Date(), enabled: false, endpoint: "endpoint", requestJson: "{}")
        scheduleService.update("123", task)
        assert scheduleService.list().size() == 1
        assert !scheduleService.get("123").enabled
        assert scheduleService.get("123").type == "hourly"
    }


    @Test
    void testCleanup() {
        assert !scheduleService.cleanup()
    }

    @Test
    void testShouldBeEnqueuedDaily() {
        ScheduledTask task1 = createTestTask("daily", Date.from(Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask("daily", Date.from(Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("daily", Date.from(Instant.now().minusSeconds(30 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("daily", Date.from(Instant.now().plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask("daily", Date.from(Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("daily", Date.from(Instant.now().plusSeconds(90 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    @Test
    void testShouldBeEnqueuedHourly() {
        ScheduledTask task1 = createTestTask("hourly", Date.from(Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask("hourly", Date.from(Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("hourly", Date.from(Instant.now().minusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("hourly", Date.from(Instant.now().plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask("hourly", Date.from(Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("hourly", Date.from(Instant.now().plusSeconds(90 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    @Test
    void testShouldBeEnqueuedImmediate() {
        ScheduledTask task1 = createTestTask(null, Date.from(Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask(null, Date.from(Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("blah", Date.from(Instant.now().minusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("immediate", Date.from(Instant.now().plusSeconds(30 * 60)))
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask(null, Date.from(Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("immed", Date.from(Instant.now().plusSeconds(90 * 60)))
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    private ScheduledTask createTestTask(String daily, Date startDate) {
        ScheduledTask task = new ScheduledTask(name: "test", type: daily, startDate: startDate, enabled: true, endpoint: "endpoint", requestJson: "{}")
        task
    }

}
