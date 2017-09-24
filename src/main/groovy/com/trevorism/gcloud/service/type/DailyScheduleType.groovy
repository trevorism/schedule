package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

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
        if(!schedule.startDate){
            return 1000 * 60 * 60 * 24
        }

        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))

        if(desiredTime.isBefore(now)){
            desiredTime = desiredTime.plusDays(1)
        }

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0){
            return 1000 * 60 * 60 * 24
        }
        return countdownTime
    }
}
