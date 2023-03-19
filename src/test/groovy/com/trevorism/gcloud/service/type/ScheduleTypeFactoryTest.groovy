package com.trevorism.gcloud.service.type

import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class ScheduleTypeFactoryTest {

    @Test
    void testCreate() {
        assert ScheduleTypeFactory.create("daily") instanceof DailyScheduleType
        assert ScheduleTypeFactory.create("hourly") instanceof HourlyScheduleType
        assert ScheduleTypeFactory.create(null) instanceof ImmediateScheduleType
        assert ScheduleTypeFactory.create("blah") instanceof ImmediateScheduleType

    }
}
