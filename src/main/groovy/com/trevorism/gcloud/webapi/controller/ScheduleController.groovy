package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import io.micronaut.http.HttpRequest
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.security.authentication.ServerAuthentication
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
    ScheduledTask create(@Body ScheduledTask schedule, HttpRequest<?> request) {
        try {
            String tenantId = tenantIdFromRequest(request)
            ScheduledTask createdSchedule = scheduleService.create(schedule, tenantId)
            return createdSchedule
        } catch (Exception e) {
            log.error("Unable to create scheduled task", e)
            throw new RuntimeException("Unable to create due to: ${e.message}")
        }
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Update a ScheduledTask **Secure")
    @Put(value = "schedule/{id}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    ScheduledTask update(String id, @Body ScheduledTask schedule) {
        try {
            ScheduledTask updatedSchedule = scheduleService.update(id, schedule)
            return updatedSchedule
        } catch (Exception e) {
            log.error("Unable to update scheduled task", e)
            throw new RuntimeException("Unable to update due to: ${e.message}")
        }
    }

    @Tag(name = "Schedule Operations")
    @Operation(summary = "Delete a ScheduledTask with the {name} **Secure")
    @Delete(value = "schedule/{name}", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.USER)
    ScheduledTask delete(String name) {
        scheduleService.delete(name)
    }


    private static String tenantIdFromRequest(HttpRequest<?> request) {
        String tenantId = null
        Optional<ServerAuthentication> wrappedTenant = request.getAttribute("micronaut.AUTHENTICATION", ServerAuthentication)
        if (wrappedTenant.isPresent())
            tenantId = wrappedTenant.get()?.attributes?.get("tenant")
        return tenantId
    }
}
