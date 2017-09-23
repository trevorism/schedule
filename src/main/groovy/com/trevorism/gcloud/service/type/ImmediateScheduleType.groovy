package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
class ImmediateScheduleType implements ScheduleType{
    @Override
    String getName() {
        return "immediate"
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        Date now = new Date()
        if(schedule.startDate && schedule.startDate.after(now)){
            return schedule.startDate.getTime() - now.getTime()
        }
        else {
            return 0
        }
    }
}
