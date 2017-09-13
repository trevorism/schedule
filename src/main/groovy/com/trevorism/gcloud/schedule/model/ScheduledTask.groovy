package com.trevorism.gcloud.schedule.model

/**
 * @author tbrooks
 */
class ScheduledTask {

    String id
    String name
    String type
    Date startDate
    boolean enabled

    String endpoint
    String httpMethod
    String requestJson
}
