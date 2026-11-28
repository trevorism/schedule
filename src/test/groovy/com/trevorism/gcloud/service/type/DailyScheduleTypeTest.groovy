package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.schedule.model.ScheduledTask
import org.junit.jupiter.api.Test

import java.time.Clock
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class DailyScheduleTypeTest {

    private static final long ONE_MINUTE = 60 * 1000L
    private static final long ONE_DAY = 24 * 60 * ONE_MINUTE

    @Test
    void testGetName() {
        def type = new DailyScheduleType()
        assert type.name == "daily"
    }

    @Test
    void testNoStartDateWillNeverEnqueue() {
        def type = new DailyScheduleType()
        assert ScheduleType.WILL_NEVER_ENQUEUE == type.getCountdownMillis(new ScheduledTask())
    }

    @Test
    void testSecondsLaterToday() {
        assert 30000L == countdown(utc(2022, 5, 9, 10, 0, 0), utc(2022, 5, 9, 10, 0, 30))
    }

    @Test
    void testSecondsLaterDuringElevenPmHour() {
        assert 30000L == countdown(utc(2026, 11, 27, 23, 5, 0), utc(2026, 11, 27, 23, 5, 30))
    }

    @Test
    void testMinutesLaterDuringElevenPmHourFromPreviousDays() {
        assert 30 * ONE_MINUTE == countdown(utc(2026, 11, 27, 23, 5, 0), utc(2026, 11, 23, 23, 35, 0))
    }

    @Test
    void testExactlyNowRollsToTomorrow() {
        assert ONE_DAY == countdown(utc(2022, 5, 9, 10, 0, 0), utc(2022, 5, 9, 10, 0, 0))
    }

    @Test
    void testEarlierTodayRollsToTomorrow() {
        assert ONE_DAY - 30 * ONE_MINUTE == countdown(utc(2022, 5, 9, 10, 30, 0), utc(2022, 5, 9, 10, 0, 0))
    }

    @Test
    void testLaterTodayFromPastStartDate() {
        assert (11 * 60 + 35) * ONE_MINUTE == countdown(utc(2022, 5, 9, 6, 55, 30), utc(2022, 5, 1, 18, 30, 30))
    }

    @Test
    void testEdgeCaseOverADayBreak() {
        assert 2400000L == countdown(utc(2022, 5, 9, 23, 50, 30), utc(2022, 5, 10, 0, 30, 30))
    }

    @Test
    void testEdgeCaseOverADayBreakFromPreviousDay() {
        assert 2400000L == countdown(utc(2022, 5, 9, 23, 50, 30), utc(2022, 5, 5, 0, 30, 30))
    }

    @Test
    void testEdgeCaseOverAYearBreak() {
        assert 3000000L == countdown(utc(2021, 12, 31, 23, 30, 30), utc(2022, 1, 1, 0, 20, 30))
    }

    @Test
    void testEdgeCaseOverAYearBreakFromPreviousYear() {
        assert 3000000L == countdown(utc(2021, 12, 31, 23, 30, 30), utc(2021, 1, 1, 0, 20, 30))
    }

    private static long countdown(ZonedDateTime now, ZonedDateTime startDate) {
        def type = new DailyScheduleType(Clock.fixed(now.toInstant(), ZoneOffset.UTC))
        type.getCountdownMillis(new ScheduledTask(startDate: Date.from(startDate.toInstant())))
    }

    private static ZonedDateTime utc(int year, int month, int day, int hour, int minute, int second) {
        ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.UTC)
    }
}
