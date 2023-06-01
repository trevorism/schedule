package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject


@Controller("/cleanup")
class CleanupController {

    @Inject
    private ScheduleService scheduleService

    @Tag(name = "Cleanup Operations")
    @Operation(summary = "Cleanup expired immediate schedules")
    @Delete(value = "/", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.SYSTEM, allowInternal = true)
    boolean cleanup() {
        scheduleService.cleanup()
    }

}
