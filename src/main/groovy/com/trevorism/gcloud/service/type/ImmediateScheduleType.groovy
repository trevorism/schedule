package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

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
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }
        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime > 0 && countdownTime < 1000 * 60 * 60){
            return countdownTime
        }

        return 0
    }
}
