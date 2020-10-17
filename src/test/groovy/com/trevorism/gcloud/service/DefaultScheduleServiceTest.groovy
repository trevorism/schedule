package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Before
import org.junit.Test

/**
 * @author tbrooks
 */
class DefaultScheduleServiceTest {

    DefaultScheduleService scheduleService = new DefaultScheduleService()
    private int addedToQueue = 0

    @Before
    void setup(){
        scheduleService.repository = new TestRepository()
        addedToQueue = 0
        scheduleService.create(TestScheduleService.createTestScheduledTask())
    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(name: "test2", type: "minute", startDate: new Date(), enabled: true, endpoint: "endpoint")
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
        ScheduledTask task = new ScheduledTask(name: "test", type: "hourly", startDate: new Date(), enabled: true, endpoint: "endpoint")
        scheduleService.update(task, "test")
        assert scheduleService.list().size() == 1
        assert scheduleService.getByName("test").enabled
        assert scheduleService.getByName("test").type == "hourly"
    }

}
