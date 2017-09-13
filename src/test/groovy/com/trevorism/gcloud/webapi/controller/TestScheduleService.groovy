package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.ScheduleService

/**
 * @author tbrooks
 */
class TestScheduleService implements ScheduleService {

    int enqueueCalled = 0
    List<ScheduledTask> tasks = []

    @Override
    ScheduledTask create(ScheduledTask schedule) {
        tasks << schedule
        return schedule
    }

    @Override
    ScheduledTask getByName(String name) {
        tasks.find{
            it.name == name
        }
    }

    @Override
    List<ScheduledTask> list() {
        tasks
    }

    @Override
    boolean delete(String name) {
        tasks.remove(getByName(name))
    }

    @Override
    void enqueue(ScheduledTask schedule) {
        enqueueCalled++
    }

    static ScheduledTask createTestScheduledTask() {
        new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false)
    }
}
