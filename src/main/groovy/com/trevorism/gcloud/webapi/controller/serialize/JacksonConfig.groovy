package com.trevorism.gcloud.webapi.controller.serialize

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

import javax.ws.rs.ext.ContextResolver
import javax.ws.rs.ext.Provider
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * @author tbrooks
 */
@Provider
class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper

    JacksonConfig() {
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        objectMapper.setDateFormat(dateFormat)
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"))
    }

    @Override
    ObjectMapper getContext(Class<?> type) {
        return objectMapper
    }
}