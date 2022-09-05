package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class ImmediateScheduleTypeTest {

    @Test
    void testGetName() {
        def type = new ImmediateScheduleType()
        assert type.name == "immediate"
    }

    @Test
    void testGetCountdownMillis() {
        def type = new ImmediateScheduleType()
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTaskNow()) == 0
    }

    @Test
    void testSecondsLaterCase(){
        def type = new ImmediateScheduleType()
        long millis = type.getCountdownMillis(TestScheduleService.createTestScheduledLater())
        assert millis <= 30000 && millis != -1
    }

    @Test
    void testPastCase(){
        def type = new ImmediateScheduleType()
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledEarlier()) == 0
    }

    @Test
    void testExactlyOneHour(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,10,12,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,10,11,30,30,0,ZoneId.of("UTC"))

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        if(countdownTime > 0 && countdownTime < ScheduleType.HOURS_IN_MILLISECONDS){
            assert false
        }
        assert countdownTime == 3600000
    }

    @Test
    void testEdgeCaseOverADayBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,5,10,0,30,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2022,5,9,23,50,30,0,ZoneId.of("UTC"))

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        assert countdownTime == 2400000
    }

    @Test
    void testEdgeCaseOverAYearBreak(){
        ZonedDateTime desiredTime = ZonedDateTime.of(2022,1,1,0,20,30,0,ZoneId.of("UTC"))
        ZonedDateTime now = ZonedDateTime.of(2021,12,31,23,30,30,0,ZoneId.of("UTC"))

        long countdownTime = desiredTime.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        assert countdownTime == 3000000
    }
}
