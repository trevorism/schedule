package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import io.micronaut.security.authentication.Authentication
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class ScheduleControllerTest {

    ScheduleController scheduleController = new ScheduleController()

    @BeforeEach
    void setup(){
        scheduleController.scheduleService = new TestScheduleService()
        scheduleController.create(TestScheduleService.createTestScheduledTaskNow(), { } as Authentication)
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
        ScheduledTask task = scheduleController.get("123")
        assert task.id == "123"
        assert task.type == "minute"
        assert !task.enabled

        assert !scheduleController.get("blah")
        assert !scheduleController.get(null)

    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(id:"22", name: "test2", type: "minute", startDate: new Date(), enabled: true)
        scheduleController.create(task, null)

        assert scheduleController.list().size() == 2
        scheduleController.delete("22", null)
        assert scheduleController.list().size() == 1

    }

    @Test
    void testDelete() {
        scheduleController.delete("123", null)
        assert scheduleController.list().size() == 0
    }

    @Test
    void testUpdate() {
        ScheduledTask task = new ScheduledTask(id: "123", name: "test", type: "progressive", startDate: new Date(), enabled: true)
        scheduleController.update("123", task)
        assert scheduleController.list().size() == 1
        assert scheduleController.get("123").enabled
        assert scheduleController.get("123").type == "progressive"

    }

}
