package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.authentication.Authentication
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("/api")
class ScheduleController {

    private static final Logger log = LoggerFactory.getLogger(ScheduleController.class.name)
    @Inject
    private ScheduleService scheduleService

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Enqueue all tasks")
    @Get(value = "enqueueAll", produces = MediaType.APPLICATION_JSON)
    boolean enqueueAll() {
        log.info("Enqueue all scheduled tasks")
        scheduleService.enqueueAll()
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Get a list of all ScheduledTasks **Secure")
    @Get(value = "schedule", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    List<ScheduledTask> list() {
        scheduleService.list()
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "View a ScheduledTask by {id} **Secure")
    @Get(value = "schedule/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    ScheduledTask get(String id) {
        scheduleService.get(id)
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Create a new ScheduledTask **Secure")
    @Post(value = "schedule", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.USER, allowInternal = true)
    ScheduledTask create(@Body ScheduledTask schedule, Authentication authentication) {
        String tenantId = tenantIdFromRequest(authentication)
        scheduleService.create(schedule, tenantId)
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Update a ScheduledTask **Secure")
    @Put(value = "schedule/{id}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    ScheduledTask update(String id, @Body ScheduledTask schedule) {
        scheduleService.update(id, schedule)
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Delete a ScheduledTask with the {id} **Secure")
    @Delete(value = "schedule/{id}", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    ScheduledTask delete(String id, Authentication authentication) {
        String tenantId = tenantIdFromRequest(authentication)
        scheduleService.delete(id, tenantId)
    }

    private static String tenantIdFromRequest(Authentication authentication) {
        return authentication?.getAttributes()?.get("tenant")
    }
}
