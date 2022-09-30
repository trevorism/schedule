package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class HourlyScheduleTypeTest {

    @Test
    void testGetName() {
        def type = new HourlyScheduleType()
        assert type.name == "hourly"
    }

    @Test
    void testSecondsLaterCase(){
        def type = new HourlyScheduleType()
        long millis = type.getCountdownMillis(TestScheduleService.createTestScheduledLater())
        assert millis <= 30100 && millis != -1
    }

    @Test
    void testPastCase(){
        def type = new HourlyScheduleType()
        def millis = type.getCountdownMillis(TestScheduleService.createTestScheduledEarlier())
        assert Math.abs(millis - 1798000) < 5000
    }

    @Test
    void testEdgeCaseOverADayBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,10,0,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,23,50,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnHourlyLogic(now, desiredTime)
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverAYearBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,1,1,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnHourlyLogic(now, desiredTime)
        assert countdownTime == 3000000
    }

    @Test
    void testFarApartSameDay(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,9,6,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,18,55,30,0,ZoneId.of("UTC"))
        long countdownTime = getCountdownMillisBasedOnHourlyLogic(now, desiredTime)
        assert countdownTime == 2100000
    }

    private long getCountdownMillisBasedOnHourlyLogic(ZonedDateTime now, ZonedDateTime desiredTime) {
        ZonedDateTime targetTime = now.withMinute(desiredTime.getMinute()).withSecond(desiredTime.getSecond())

        if (targetTime == now) {
            assert false
        }

        if (targetTime.isBefore(now)) {
            targetTime = targetTime.plusHours(1)
        }

        return targetTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
    }
}
