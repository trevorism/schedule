package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

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
    void testGetCountdownMillis() {
        def type = new HourlyScheduleType()
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTaskNow()) == -1
    }
}
