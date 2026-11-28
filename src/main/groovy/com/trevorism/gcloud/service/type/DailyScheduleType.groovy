package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class DailyScheduleType implements ScheduleType {

    private final Clock clock

    DailyScheduleType() {
        this(Clock.systemUTC())
    }

    DailyScheduleType(Clock clock) {
        this.clock = clock
    }

    @Override
    String getName() {
        ScheduleTypeFactory.DAILY
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }

        ZonedDateTime now = Instant.now(clock).atZone(ZoneOffset.UTC)
        LocalTime desiredTime = schedule.startDate.toInstant().atZone(ZoneOffset.UTC).toLocalTime()
        ZonedDateTime targetTime = now.with(desiredTime)

        if(!targetTime.isAfter(now)){
            targetTime = targetTime.plusDays(1)
        }

        return Duration.between(now, targetTime).toMillis()
    }
}
