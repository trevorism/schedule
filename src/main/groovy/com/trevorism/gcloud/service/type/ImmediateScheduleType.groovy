package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 * Immediate runs if at the start date,
 * or immediately if the start date has already occurred
 */
class ImmediateScheduleType implements ScheduleType{

    @Override
    String getName() {
        ScheduleTypeFactory.IMMEDIATE
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }
        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime > 0 && countdownTime < HOURS_IN_MILLISECONDS){
            return countdownTime
        }

        return 0
    }
}
