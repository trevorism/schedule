package com.trevorism.gcloud.service

import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions
import com.google.gson.Gson
import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory

/**
 * @author tbrooks
 */
class DefaultScheduleService implements ScheduleService {

    private Repository<ScheduledTask> repository = new PingingDatastoreRepository<>(ScheduledTask)
    private Gson gson = new Gson()
    private com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getDefaultQueue()

    @Override
    ScheduledTask create(ScheduledTask schedule) {
        repository.create(schedule)
    }

    @Override
    ScheduledTask getByName(String name) {
        repository.list().find{
            it.name == name
        }
    }

    @Override
    List<ScheduledTask> list() {
        repository.list()
    }

    @Override
    boolean delete(String name) {
        ScheduledTask task = getByName(name)
        if(task)
            repository.delete(task.id)
    }

    @Override
    void enqueue(ScheduledTask schedule) {
        ScheduleType type = ScheduleTypeFactory.create(schedule.type)
        String json = gson.toJson(schedule)
        long countdownMillis = type.getCountdownMillis(schedule)
        TaskOptions taskOptions = taskOptions(countdownMillis, json)
        queue.add(taskOptions)
    }

    private TaskOptions taskOptions(long countdownMillis, String json) {
        TaskOptions taskOptions = TaskOptions.Builder.withCountdownMillis(countdownMillis)
        taskOptions.payload(json, "UTF-8")
        taskOptions.removeHeader("Content-Type")
        taskOptions.headers(["Content-Type": "application/json"])
        taskOptions
    }


}
