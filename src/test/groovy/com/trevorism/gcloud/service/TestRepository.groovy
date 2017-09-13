package com.trevorism.gcloud.service

import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
class TestRepository implements Repository<ScheduledTask> {

    List<ScheduledTask> tasks = []

    @Override
    List<ScheduledTask> list() {
        tasks
    }

    @Override
    ScheduledTask get(String id) {
        tasks.find{
            it.id == id
        }
    }

    @Override
    ScheduledTask create(ScheduledTask scheduledTask) {
        scheduledTask.id = new Random().nextInt(100000).toString()
        tasks << scheduledTask
        return scheduledTask
    }

    @Override
    ScheduledTask update(String s, ScheduledTask scheduledTask) {
        return null
    }

    @Override
    ScheduledTask delete(String id) {
        ScheduledTask task = get(id)
        tasks.remove(task)
        return task
    }

    @Override
    void ping() {

    }
}
