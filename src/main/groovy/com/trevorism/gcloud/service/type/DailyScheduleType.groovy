package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

/**
 * @author tbrooks
 */
class DailyScheduleType implements ScheduleType {
    @Override
    String getName() {
        return "daily"
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        Date now = new Date()
        if(schedule.startDate.after(now)){
            return schedule.startDate.getTime() - now.getTime()
        }
        else {
            return 1000 * 60 * 60 * 24
        }
    }
}
