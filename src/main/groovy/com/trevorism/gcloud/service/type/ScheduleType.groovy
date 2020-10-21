package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleType {
    static long WILL_NEVER_ENQUEUE = -1

    String getName()
    long getCountdownMillis(ScheduledTask schedule)

}