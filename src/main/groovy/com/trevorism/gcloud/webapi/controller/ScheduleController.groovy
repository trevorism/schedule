package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import java.util.logging.Logger

/**
 * @author tbrooks
 */
@Api("Schedule Operations")
@Path("/api")
class ScheduleController {

    private static final Logger log = Logger.getLogger(ScheduleController.class.name)
    private ScheduleService scheduleService = new DefaultScheduleService()

    @ApiOperation(value = "Enqueue all tasks **Secure")
    @GET
    @Path("enqueueAll")
    @Produces(MediaType.APPLICATION_JSON)
    boolean enqueueAll() {
        log.info("Enqueue all scheduled tasks")
        scheduleService.enqueueAll()
    }

    @ApiOperation(value = "Get a list of all ScheduledTasks")
    @GET
    @Path("schedule")
    @Secure(Roles.USER)
    @Produces(MediaType.APPLICATION_JSON)
    List<ScheduledTask> list() {
        scheduleService.list()
    }

    @ApiOperation(value = "View a ScheduledTask with the {name}")
    @GET
    @Path("schedule/{name}")
    @Secure(Roles.USER)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask get(@PathParam("name") String name) {
        scheduleService.getByName(name)
    }

    @ApiOperation(value = "Create a new ScheduledTask **Secure")
    @POST
    @Path("schedule")
    @Secure(Roles.SYSTEM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask create(ScheduledTask schedule) {
        ScheduledTask createdSchedule = scheduleService.create(schedule)
        return createdSchedule
    }

    @ApiOperation(value = "Update a ScheduledTask **Secure")
    @PUT
    @Path("schedule/{name}")
    @Secure(Roles.SYSTEM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask update(@PathParam("name") String name, ScheduledTask schedule) {
        ScheduledTask updatedSchedule = scheduleService.update(schedule, name)
        return updatedSchedule
    }

    @ApiOperation(value = "Delete a ScheduledTask with the {name} **Secure")
    @DELETE
    @Secure(Roles.SYSTEM)
    @Path("schedule/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    boolean delete(@PathParam("name") String name) {
        scheduleService.delete(name)
    }

}
