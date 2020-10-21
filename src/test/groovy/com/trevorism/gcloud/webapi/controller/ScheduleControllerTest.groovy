package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import org.junit.Before
import org.junit.Test

/**
 * @author tbrooks
 */
class ScheduleControllerTest {

    ScheduleController scheduleController = new ScheduleController()

    @Before
    void setup(){
        scheduleController.scheduleService = new TestScheduleService()
        scheduleController.create(TestScheduleService.createTestScheduledTaskNow())
    }

    @Test
    void testList() {
        def list = scheduleController.list()
        assert list
        assert list.size() == 1
        assert list[0].name == "test"
        assert list[0].type == "minute"
        assert !list[0].enabled

    }

    @Test
    void testGet() {
        ScheduledTask task = scheduleController.get("test")
        assert task.name == "test"
        assert task.type == "minute"
        assert !task.enabled

        assert !scheduleController.get("blah")
        assert !scheduleController.get(null)

    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(name: "test2", type: "minute", startDate: new Date(), enabled: true)
        scheduleController.create(task)

        assert scheduleController.list().size() == 2
        scheduleController.delete("test2")
        assert scheduleController.list().size() == 1

    }

    @Test
    void testDelete() {
        scheduleController.delete("test")
        assert scheduleController.list().size() == 0

    }

    @Test
    void testUpdate() {
        ScheduledTask task = new ScheduledTask(name: "test", type: "progressive", startDate: new Date(), enabled: true)
        scheduleController.update("test", task)
        assert scheduleController.list().size() == 1
        assert scheduleController.get("test").enabled
        assert scheduleController.get("test").type == "progressive"

    }

}
