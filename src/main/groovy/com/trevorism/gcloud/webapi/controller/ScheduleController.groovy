package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Secure
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * @author tbrooks
 */
@Api("Schedule Operations")
@Path("/api")
class ScheduleController {

    private ScheduleService scheduleService = new DefaultScheduleService()

    @ApiOperation(value = "Get a list of all ScheduledTasks")
    @GET
    @Path("schedule")
    @Produces(MediaType.APPLICATION_JSON)
    List<ScheduledTask> list() {
        scheduleService.list()
    }

    @ApiOperation(value = "View a ScheduledTask with the {name}")
    @GET
    @Path("schedule/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask get(@PathParam("name") String name) {
        scheduleService.getByName(name)
    }

    @ApiOperation(value = "Create a new ScheduledTask **Secure")
    @POST
    @Path("schedule")
    @Secure
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask create(ScheduledTask schedule) {
        ScheduledTask createdSchedule = scheduleService.create(schedule)
        return createdSchedule
    }

    @ApiOperation(value = "Update a ScheduledTask **Secure")
    @PUT
    @Path("schedule/{name}")
    @Secure
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask update(@PathParam("name") String name, ScheduledTask schedule) {
        ScheduledTask updatedSchedule = scheduleService.update(schedule, name)
        return updatedSchedule
    }

    @ApiOperation(value = "Delete a ScheduledTask with the {name} **Secure")
    @DELETE
    @Secure
    @Path("schedule/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    boolean delete(@PathParam("name") String name) {
        scheduleService.delete(name)
    }


}
