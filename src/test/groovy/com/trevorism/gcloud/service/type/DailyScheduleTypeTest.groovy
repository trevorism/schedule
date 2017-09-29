package com.trevorism.gcloud.service.type

import com.fasterxml.jackson.databind.ObjectMapper
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

import java.text.DateFormat
import java.text.SimpleDateFormat

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
