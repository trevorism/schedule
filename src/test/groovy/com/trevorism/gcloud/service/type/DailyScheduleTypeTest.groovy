package com.trevorism.gcloud.service.type


import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

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

        assert -1L == type.getCountdownMillis(TestScheduleService.createTestScheduledEarlier())
        assert -1L == type.getCountdownMillis(TestScheduleService.createTestScheduledTaskNow())
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) > 0
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledLater()) < 1000 * 60 * 60

    }

}
