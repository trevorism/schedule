package com.trevorism.gcloud.schedule.model

import io.swagger.annotations.ApiModelProperty

/**
 * @author tbrooks
 */
class ScheduledTask {

    @ApiModelProperty(value = "An id value", dataType = "string", allowableValues = "range[1,2147483647]")
    String id
    @ApiModelProperty(value = "Unique name of the task")
    String name

    @ApiModelProperty(value = "The type of scheduling task", allowableValues = "daily,hourly,immediate")
    String type
    @ApiModelProperty(value = "When the task will start")
    Date startDate
    @ApiModelProperty(value = "Is this task enabled?")
    boolean enabled

    @ApiModelProperty(value = "Endpoint for the task")
    String endpoint
    @ApiModelProperty(value = "HTTP Method for the task", allowableValues = "get,post,put,delete")
    String httpMethod = "post"

    @ApiModelProperty(value = "For POST and PUT, the request body", allowableValues = "get,post,put,delete")
    String requestJson
}
