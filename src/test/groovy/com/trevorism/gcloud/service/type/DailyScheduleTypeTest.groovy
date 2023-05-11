package com.trevorism.gcloud.service.type


import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.jupiter.api.Test

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class DailyScheduleTypeTest {

    @Test
    void testGetName() {
        def type = new DailyScheduleType()
        assert type.name == "daily"
    }

    @Test
    void testGetCountdownMillis() {
        def type = new DailyScheduleType()

        long millisForEarlier = type.getCountdownMillis(TestScheduleService.createTestScheduledEarlier())

        assert -1L == type.getCountdownMillis(TestScheduleService.createTestScheduledTaskNow())
        assert -1L == millisForEarlier || 84600000L == millisForEarlier //this can happen if the hour UTC > 23
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) > 0
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) < 1000 * 60 * 60

    }

    @Test
    void testSecondsLaterCase(){
        def type = new DailyScheduleType()
        long millis = type.getCountdownMillis(TestScheduleService.createTestScheduledLater())
        assert millis <= 30100 && millis != -1
    }

    @Test
    void testEdgeCaseOverADayBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,10,0,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverADayBreakFromPreviousDay(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,5,0,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testFarApartSameDay(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,6,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,18,55,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == -1
    }

    @Test
    void testFarApartSameDayInFuture(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,18,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,6,55,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == -1
    }

    @Test
    void testEdgeCaseOverAYearBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,1,1,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == 3000000
    }

    @Test
    void testEdgeCaseOverAYearBreakFromPreviousYear(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2021,1,1,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnDailyLogic(now, desiredTime)
        assert countdownTime == 3000000
    }

    private long getCountdownMillisBasedOnDailyLogic(ZonedDateTime now, ZonedDateTime desiredTime) {
        ZonedDateTime targetTime = now.withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        if(now.getHour() == 23){
            ZonedDateTime myDay = now.plusDays(1)
            targetTime = targetTime.withYear(myDay.getYear()).withMonth(myDay.getMonthValue())
                    .withDayOfMonth(myDay.getDayOfMonth()).withHour(desiredTime.getHour())
        }

        long countdownTime = targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0){
            return -1
        }
        return countdownTime
    }

}
