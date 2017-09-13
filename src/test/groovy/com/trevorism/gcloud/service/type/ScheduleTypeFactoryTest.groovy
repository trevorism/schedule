package com.trevorism.gcloud.service.type

import org.junit.Test

/**
 * @author tbrooks
 */
class ScheduleTypeFactoryTest {
    @Test
    void testCreate() {
        assert ScheduleTypeFactory.create("minute") instanceof MinuteScheduleType
        assert ScheduleTypeFactory.create("daily") instanceof DailyScheduleType
        assert ScheduleTypeFactory.create(null) instanceof ImmediateScheduleType
        assert ScheduleTypeFactory.create("blah") instanceof ImmediateScheduleType

    }
}
