package com.trevorism.gcloud.webapi.controller.inject

import org.junit.Test

import javax.ws.rs.container.ContainerRequestContext

/**
 * @author tbrooks
 */
class LoggingRequestFilterTest {

    @Test
    void testFilterNullCorrelationId() {
        ContainerRequestContext requestContext = [getHeaderString: {String name -> return null}] as ContainerRequestContext
        callFilterAndAssertNoException(requestContext)
    }

    @Test
    void testFilterValidCorrelationId() {
        ContainerRequestContext requestContext = [getHeaderString: {String name -> return "blah"}] as ContainerRequestContext
        callFilterAndAssertNoException(requestContext)
    }

    private void callFilterAndAssertNoException(ContainerRequestContext requestContext) {
        LoggingRequestFilter filter = new LoggingRequestFilter()
        try {
            filter.filter(requestContext)
            assert true
        }
        catch (Exception e) {
            assert false
        }
    }
}
