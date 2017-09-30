package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class ProgressiveScheduleType implements ScheduleType {

    DailyScheduleType dailyScheduleType = new DailyScheduleType()
    HourlyScheduleType hourlyScheduleType = new HourlyScheduleType()

    @Override
    String getName() {
        return "progressive"
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            throw new RuntimeException("Cannot progressively schedule without a start date")
        }

        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))

        desiredTime = desiredTime.plusDays(2)
        if(desiredTime.isBefore(now)){
            return hourlyScheduleType.getCountdownMillis(schedule)
        }
        return dailyScheduleType.getCountdownMillis(schedule)

    }
}
