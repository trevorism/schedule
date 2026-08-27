package com.trevorism.gcloud.schedule.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema


/**
 * @author tbrooks
 */
class ScheduledTask {

    @Schema(description = "An id value", type = "String", allowableValues = "range[1,9223372036854775807]")
    String id
    @Schema(description = "Unique name of the task")
    String name

    @Schema(description = "The type of scheduling task (daily, hourly, or immediate)", example = "daily")
    String type
    @Schema(description = "When the task will start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date startDate
    @Schema(description = "Is this task enabled?")
    boolean enabled

    @Schema(description = "Endpoint for the task")
    String endpoint
    @Schema(description = "HTTP Method for the task (get, post, put, patch, or delete)", example = "post")
    String httpMethod = "post"

    @Schema(description = "For POST, PUT, and PATCH the request body")
    String requestJson

    @Schema(description = "The tenantId of the schedule", type = "String")
    String tenantId
}
