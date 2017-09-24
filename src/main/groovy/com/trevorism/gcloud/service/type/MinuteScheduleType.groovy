package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
class MinuteScheduleType implements ScheduleType {
    @Override
    String getName() {
        return "minute"
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        Date now = new Date()
        if(schedule.startDate && schedule.startDate.after(now)){
            return schedule.startDate.getTime() - now.getTime()
        }
        return 1000 * 60
    }
}
