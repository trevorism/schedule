package com.trevorism.gcloud.schedule.model

import com.trevorism.PropertiesProvider

class DatastoreTokenRequest {
    String id
    String password
    String audience = "6ba426e4-f740-44b5-98ce-15a5bc4ed105"

    static get(PropertiesProvider propertiesProvider){
        return new DatastoreTokenRequest(id: propertiesProvider.getProperty("clientId") , password: propertiesProvider.getProperty("clientSecret"))
    }
}
