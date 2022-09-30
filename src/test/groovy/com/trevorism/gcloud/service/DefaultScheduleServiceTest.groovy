package com.trevorism.gcloud.service

import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Before
import org.junit.Test

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * @author tbrooks
 */
class DefaultScheduleServiceTest {

    DefaultScheduleService scheduleService = new DefaultScheduleService()
    private int addedToQueue = 0

    @Before
    void setup() {
        scheduleService.repository = new TestRepository()
        addedToQueue = 0
        scheduleService.create(TestScheduleService.createTestScheduledTaskNow())
    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(name: "test2", type: "minute", startDate: new Date(), enabled: false, endpoint: "endpoint", httpMethod: "get")
        ScheduledTask createdTask = scheduleService.create(task)
        assert scheduleService.list().size() == 2
        assert createdTask.id
        scheduleService.delete("test2")
        assert scheduleService.list().size() == 1
    }

    @Test
    void testGetByName() {
        ScheduledTask task = scheduleService.getByName("test")
        assert task.name == "test"
        assert task.id
        assert !task.enabled
    }

    @Test
    void testList() {
        assert scheduleService.list()
        assert scheduleService.list().size() == 1
    }

    @Test
    void testDelete() {
        scheduleService.delete("test")
        assert scheduleService.list().size() == 0
    }

    @Test
    void testUpdate() {
        ScheduledTask task = new ScheduledTask(name: "test", type: "hourly", startDate: new Date(), enabled: false, endpoint: "endpoint", requestJson: "{}")
        scheduleService.update(task, "test")
        assert scheduleService.list().size() == 1
        assert !scheduleService.getByName("test").enabled
        assert scheduleService.getByName("test").type == "hourly"
    }


    @Test
    void testCleanup(){
        assert !scheduleService.cleanup()
    }

    @Test
    void testShouldBeEnqueuedDaily() {
        ScheduledTask task1 = createTestTask("daily", Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask("daily", Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("daily", Instant.now().minusSeconds(30 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("daily", Instant.now().plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask("daily", Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("daily", Instant.now().plusSeconds(90 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    @Test
    void testShouldBeEnqueuedHourly() {
        ScheduledTask task1 = createTestTask("hourly", Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask("hourly", Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("hourly", Instant.now().minusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("hourly", Instant.now().plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask("hourly", Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("hourly", Instant.now().plusSeconds(90 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    @Test
    void testShouldBeEnqueuedImmediate() {
        ScheduledTask task1 = createTestTask(null, Instant.now().minus(4, ChronoUnit.DAYS).minusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task1, ScheduleTypeFactory.create(task1.type))

        ScheduledTask task2 = createTestTask(null, Instant.now().minus(4, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task2, ScheduleTypeFactory.create(task2.type))

        ScheduledTask task3 = createTestTask("blah", Instant.now().minusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task3, ScheduleTypeFactory.create(task3.type))

        ScheduledTask task4 = createTestTask("immediate", Instant.now().plusSeconds(30 * 60).toDate())
        assert DefaultScheduleService.shouldBeEnqueued(task4, ScheduleTypeFactory.create(task4.type))

        ScheduledTask task5 = createTestTask(null, Instant.now().plus(1, ChronoUnit.DAYS).plusSeconds(30 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task5, ScheduleTypeFactory.create(task5.type))

        ScheduledTask task6 = createTestTask("immed", Instant.now().plusSeconds(90 * 60).toDate())
        assert !DefaultScheduleService.shouldBeEnqueued(task6, ScheduleTypeFactory.create(task6.type))
    }

    private ScheduledTask createTestTask(String daily, Date startDate) {
        ScheduledTask task = new ScheduledTask(name: "test", type: daily, startDate: startDate, enabled: true, endpoint: "endpoint", requestJson: "{}")
        task
    }
}
