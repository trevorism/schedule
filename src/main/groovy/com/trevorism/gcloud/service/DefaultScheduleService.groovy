package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory

import java.util.logging.Logger

/**
 * @author tbrooks
 */
class DefaultScheduleService implements ScheduleService {

    private static final Logger log = Logger.getLogger(DefaultScheduleService.class.name)

    private Repository<ScheduledTask> repository = new PingingDatastoreRepository<>(ScheduledTask)
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
    private ScheduledTaskValidator validator = new ScheduledTaskValidator(this)

    @Override
    ScheduledTask create(ScheduledTask schedule) {
        schedule = ScheduledTaskValidator.cleanup(schedule)
        validator.validate(schedule, false)
        schedule = repository.create(schedule)
        enqueue(schedule)
        return schedule
    }

    @Override
    ScheduledTask getByName(String name) {
        repository.list().find {
            it.name == name
        }
    }

    @Override
    ScheduledTask update(ScheduledTask schedule, String name) {
        schedule = ScheduledTaskValidator.cleanup(schedule)
        validator.validate(schedule, true)

        ScheduledTask existingTask = getByName(name)
        schedule = repository.update(existingTask.id, schedule)

        enqueueAll()
        return schedule
    }

    @Override
    List<ScheduledTask> list() {
        repository.list()
    }

    @Override
    boolean delete(String name) {
        ScheduledTask task = getByName(name)
        if (task) {
            repository.delete(task.id)
        }
    }

    @Override
    void enqueue(ScheduledTask schedule) {
        ScheduleType type = ScheduleTypeFactory.create(schedule.type)
        String json = gson.toJson(schedule)
        long countdownMillis = type.getCountdownMillis(schedule)
    }

    @Override
    boolean enqueueAll() {
        Thread.sleep(5000)
        def list = repository.list()
        list.each { ScheduledTask st ->
            enqueue(st)
        }
        return list
    }


}
