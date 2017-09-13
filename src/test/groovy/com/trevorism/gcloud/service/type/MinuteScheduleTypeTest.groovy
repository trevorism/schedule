package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

/**
 * @author tbrooks
 */
class MinuteScheduleTypeTest {

    @Test
    void testGetName() {
        def type = new MinuteScheduleType()
        assert type.name == "minute"
    }

    @Test
    void testGetCountdownMillis() {
        def type = new MinuteScheduleType()
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTask()) == 60 * 1000
    }
}
