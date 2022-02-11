package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleService {

    ScheduledTask create(ScheduledTask schedule)
    ScheduledTask getByName(String name)
    ScheduledTask update(ScheduledTask scheduledTask, String name)
    List<ScheduledTask> list()
    boolean delete(String name)

    void enqueue(ScheduledTask schedule)
    boolean enqueueAll()

    boolean cleanup()
}
