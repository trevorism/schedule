package com.trevorism.gcloud.webapi.controller

import org.junit.Test

/**
 * @author tbrooks
 */
class RootControllerTest {

    @Test
    void testRootControllerEndpoints(){
        RootController rootController = new RootController()
        assert rootController.endpoints.contains("ping")
        assert rootController.endpoints.contains("help")
        assert rootController.endpoints.contains("api/schedule")
    }

    @Test
    void testRootControllerPing(){
        RootController rootController = new RootController()
        assert rootController.ping() == "pong"
    }
}
