package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import org.junit.Test

import java.time.Instant

/**
 * @author tbrooks
 */
class RootControllerTest {

    @Test
    void testRootControllerEndpoints(){
        RootController rootController = new RootController()
        assert rootController.displayHelpLink().contains("help")
    }

    @Test
    void testRootControllerPing(){
        RootController rootController = new RootController()
        assert rootController.ping() == "pong"
    }

}
