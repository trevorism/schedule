package com.trevorism.gcloud.webapi.controller.inject

import javax.ws.rs.Path
import javax.ws.rs.container.DynamicFeature
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.FeatureContext
import javax.ws.rs.ext.Provider

/**
 * @author tbrooks
 */
@Provider
class RequestInterceptor implements DynamicFeature{
    @Override
    void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (resourceInfo.getResourceClass().getAnnotation(Path) == null)
            return

        context.register(LoggingRequestFilter)
    }
}
