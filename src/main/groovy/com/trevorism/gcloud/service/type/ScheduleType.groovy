package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleType {

    String getName()
    long getCountdownMillis(ScheduledTask schedule)

}