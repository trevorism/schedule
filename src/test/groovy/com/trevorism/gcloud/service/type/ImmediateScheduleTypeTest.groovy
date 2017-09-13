package com.trevorism.gcloud.service.type

import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

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
        assert type.getCountdownMillis(TestScheduleService.createTestScheduledTask()) == 0
    }
}
