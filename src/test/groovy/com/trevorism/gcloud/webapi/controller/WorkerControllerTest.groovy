package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import org.junit.Before
import org.junit.Test

/**
 * @author tbrooks
 */
class WorkerControllerTest {

    WorkerController workerController = new WorkerController()

    @Before
    void setup(){
        workerController.scheduleService = new TestScheduleService()
        ScheduledTask task = TestScheduleService.createTestScheduledTask()
        task.enabled = true
        workerController.scheduleService.create(task)
    }

    @Test
    void testPerformAction() {
        workerController.performAction("blah")
        assert workerController.scheduleService.enqueueCalled == 0

        workerController.performAction("test")
        assert workerController.scheduleService.enqueueCalled == 1

    }
}
