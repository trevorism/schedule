package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class DailyScheduleType implements ScheduleType {
    @Override
    String getName() {
        ScheduleTypeFactory.DAILY
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }

        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))
        ZonedDateTime targetTime = ZonedDateTime.now(ZoneId.of("UTC")).withHour(desiredTime.getHour()).withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        if(now.getHour() == 23) {
            ZonedDateTime myDay = now.plusDays(1)
            targetTime = targetTime.withYear(myDay.getYear()).withMonth(myDay.getMonthValue())
                    .withDayOfMonth(myDay.getDayOfMonth()).withHour(desiredTime.getHour())
        }

        long countdownTime = targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0){
            return WILL_NEVER_ENQUEUE
        }
        return countdownTime
    }
}
