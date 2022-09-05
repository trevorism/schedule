package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
interface ScheduleType {

    static final long WILL_NEVER_ENQUEUE = -1
    static final long HOURS_IN_MILLISECONDS = 1000 * 60 * 60

    String getName()
    long getCountdownMillis(ScheduledTask schedule)

}