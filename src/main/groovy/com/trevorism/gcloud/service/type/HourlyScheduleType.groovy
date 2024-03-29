package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class HourlyScheduleType implements ScheduleType {

    @Override
    String getName() {
        ScheduleTypeFactory.HOURLY
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }

        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))
        ZonedDateTime targetTime = ZonedDateTime.now(ZoneId.of("UTC")).withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        if(targetTime == now){
            return 0
        }

        if(targetTime.isBefore(now)){
            targetTime = targetTime.plusHours(1)
        }

        long countdownTime = targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0){
            return WILL_NEVER_ENQUEUE
        }
        return countdownTime
    }
}