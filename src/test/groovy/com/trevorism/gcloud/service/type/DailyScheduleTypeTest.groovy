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
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTask()) <= 1000 * 60 * 60 * 24
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTask()) > 86390000
    }
}
