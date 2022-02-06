package com.trevorism.gcloud

import com.trevorism.http.headers.HeadersHttpClient
import com.trevorism.http.headers.HeadersJsonHttpClient
import com.trevorism.http.util.ResponseUtils
import com.trevorism.secure.PropertiesProvider

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

PropertiesProvider propertiesProvider = new PropertiesProvider()
HeadersHttpClient jsonHttpClient = new HeadersJsonHttpClient()
def response = ""

When(/the endpoint tester internal endpoint is invoked/) { ->
    def token = propertiesProvider.getProperty("scheduleToken")
    response = ResponseUtils.getEntity(jsonHttpClient.get("https://endpoint-tester.testing.trevorism.com/secure/internal", ["Authorization": "bearer $token".toString()]))
}

Then(/a response is returned successfully/) { ->
    assert response == "secure internal"
}
