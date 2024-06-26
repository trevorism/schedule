package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleService {

    ScheduledTask create(ScheduledTask schedule, String tenantId)

    List<ScheduledTask> list()
    ScheduledTask get(String id)
    ScheduledTask update(String id, ScheduledTask scheduledTask)
    ScheduledTask delete(String id)


    void enqueue(ScheduledTask schedule)
    boolean enqueueAll()

    boolean cleanup()
}
