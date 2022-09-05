package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

import java.time.ZoneId
import java.time.ZonedDateTime

class WeeklyScheduleTypeTest {

    @Test
    void testGetName() {
        def type = new WeeklyScheduleType()
        assert type.name == "weekly"
    }

    @Test
    void testGetCountdownMillis() {
        def type = new WeeklyScheduleType()

        assert -1L == type.getCountdownMillis(TestScheduleService.createTestScheduledEarlier())
        assert -1L == type.getCountdownMillis(TestScheduleService.createTestScheduledTaskNow())
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) > 0
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) < 1000 * 60 * 60

    }

    @Test
    void testSecondsLaterCase(){
        def type = new WeeklyScheduleType()
        long millis = type.getCountdownMillis(TestScheduleService.createTestScheduledLater())
        assert millis <= 30000 && millis != -1
    }

    @Test
    void testEdgeCaseOverADayBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,10,0,30,30,0, ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverADayBreak2(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,0,30,30,0, ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,8,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverADayBreak3(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,8,0,30,30,0, ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,7,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testFarApartSameDay(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,6,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,18,55,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == -1
    }

    @Test
    void testFarApartSameDayInFuture(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,18,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,6,55,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == -1
    }

    @Test
    void testEdgeCaseOverADayBreakFromPreviousWeek(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,8,0,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,14,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverAYearBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,12,24,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 3000000
    }

    @Test
    void testEdgeCaseOverAYearBreakFromPreviousYear(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2021,1,2,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnWeeklyLogic(now, desiredTime)
        assert countdownTime == 3000000
    }

    private long getCountdownMillisBasedOnWeeklyLogic(ZonedDateTime now, ZonedDateTime desiredTime) {
        int addDays = Math.abs(desiredTime.getDayOfWeek().ordinal() - now.getDayOfWeek().ordinal())
        if(addDays == 6)
            addDays = 1

        ZonedDateTime myDay = now.plusDays(addDays)
        ZonedDateTime targetTime = now.withYear(myDay.getYear()).withMonth(myDay.getMonthValue())
                .withDayOfMonth(myDay.getDayOfMonth()).withHour(desiredTime.getHour())
                .withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        long countdownTime = targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime <= 0 || countdownTime > ScheduleType.HOURS_IN_MILLISECONDS){
            return -1
        }
        return countdownTime
    }
}
