package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask

import java.time.ZoneId
import java.time.ZonedDateTime

class WeeklyScheduleType implements ScheduleType{

    @Override
    String getName() {
        ScheduleTypeFactory.WEEKLY
    }

    @Override
    long getCountdownMillis(ScheduledTask schedule) {
        if(!schedule.startDate){
            return WILL_NEVER_ENQUEUE
        }

        ZonedDateTime desiredTime = schedule.startDate.toInstant().atZone(ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"))

        int addDays = Math.abs(desiredTime.getDayOfWeek().ordinal() - now.getDayOfWeek().ordinal())
        //Handles edge case when the ordinals of adjacent days of the week are far apart
        if(addDays == 6)
            addDays = 1

        ZonedDateTime myDay = now.plusDays(addDays)
        ZonedDateTime targetTime = now.withYear(myDay.getYear()).withMonth(myDay.getMonthValue())
                .withDayOfMonth(myDay.getDayOfMonth()).withHour(desiredTime.getHour())
                .withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        long countdownTime = targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0 || countdownTime > HOURS_IN_MILLISECONDS){
            return WILL_NEVER_ENQUEUE
        }
        return countdownTime
    }
}
