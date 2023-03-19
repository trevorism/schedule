package com.trevorism.gcloud

import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.ClasspathBasedPropertiesProvider
import com.trevorism.PropertiesProvider

this.metaClass.mixin(io.cucumber.groovy.Hooks)
this.metaClass.mixin(io.cucumber.groovy.EN)

PropertiesProvider propertiesProvider = new ClasspathBasedPropertiesProvider()
HttpClient jsonHttpClient = new JsonHttpClient()
def response = ""

When(/the endpoint tester internal endpoint is invoked/) { ->
    def token = propertiesProvider.getProperty("token")
    response = jsonHttpClient.get("https://endpoint-tester.testing.trevorism.com/secure/internal", ["Authorization": "bearer $token".toString()]).value
}

Then(/a response is returned successfully/) { ->
    assert response == "secure internal"
}
