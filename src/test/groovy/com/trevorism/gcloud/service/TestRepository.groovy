package com.trevorism.gcloud.service

import com.trevorism.data.Repository
import com.trevorism.data.model.filtering.ComplexFilter
import com.trevorism.data.model.paging.PageRequest
import com.trevorism.data.model.sorting.ComplexSort
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
        list()
    }

    @Override
    ScheduledTask get(String id) {
        tasks.find{
            it.id == id
        }
    }

    @Override
    ScheduledTask get(String s, String s1) {
        get(s)
    }

    @Override
    ScheduledTask create(ScheduledTask scheduledTask) {
        scheduledTask.id = new Random().nextInt(100000).toString()
        tasks << scheduledTask
        return scheduledTask
    }

    @Override
    ScheduledTask create(ScheduledTask scheduledTask, String s) {
        create(scheduledTask)
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
        delete(s)
    }

    @Override
    void ping() {

    }

    @Override
    List<ScheduledTask> filter(ComplexFilter complexFilter) {
        return list().findAll(){
            it.name == complexFilter.simpleFilters[0].value
        }
    }

    @Override
    List<ScheduledTask> page(PageRequest pageRequest) {
        return list()
    }

    @Override
    List<ScheduledTask> sort(ComplexSort complexSort) {
        return list()
    }
}
