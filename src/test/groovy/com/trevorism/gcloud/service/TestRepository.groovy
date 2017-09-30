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
    List<ScheduledTask> list(String s) {
        list(null)
    }

    @Override
    ScheduledTask get(String id) {
        tasks.find{
            it.id == id
        }
    }

    @Override
    ScheduledTask get(String s, String s1) {
        get(s, null)
    }

    @Override
    ScheduledTask create(ScheduledTask scheduledTask) {
        scheduledTask.id = new Random().nextInt(100000).toString()
        tasks << scheduledTask
        return scheduledTask
    }

    @Override
    ScheduledTask create(ScheduledTask scheduledTask, String s) {
        create(scheduledTask, null)
    }

    @Override
    ScheduledTask update(String s, ScheduledTask scheduledTask) {
        update(s, scheduledTask, null)
    }

    @Override
    ScheduledTask update(String s, ScheduledTask scheduledTask, String s1) {
        tasks.remove(get(s))
        create(scheduledTask)
        return scheduledTask
    }

    @Override
    ScheduledTask delete(String id) {
        ScheduledTask task = get(id)
        tasks.remove(task)
        return task
    }

    @Override
    ScheduledTask delete(String s, String s1) {
        delete(s, null)
    }

    @Override
    void ping() {

    }
}
