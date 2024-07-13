package com.trevorism.gcloud.service

import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleService {

    ScheduledTask create(ScheduledTask schedule, String tenantId)

    List<ScheduledTask> list()
    ScheduledTask get(String id)
    ScheduledTask update(String id, ScheduledTask scheduledTask)
    ScheduledTask delete(String id, String tenantId)

    void enqueue(ScheduledTask schedule, Repository<ScheduledTask> repository)
    boolean enqueueAll()

    boolean cleanup()
}
