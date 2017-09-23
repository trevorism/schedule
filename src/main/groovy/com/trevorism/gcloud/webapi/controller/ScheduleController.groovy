package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.secure.Secure
import io.swagger.annotations.Api

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @author tbrooks
 */
@Api("Schedule Operations")
@Path("/api")
class ScheduleController {

    private ScheduleService scheduleService = new DefaultScheduleService()

    @GET
    @Path("schedule")
    @Produces(MediaType.APPLICATION_JSON)
    List<ScheduledTask> list(){
        scheduleService.list()
    }

    @GET
    @Path("schedule/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask get(@PathParam("name") String name){
        scheduleService.getByName(name)
    }

    @POST
    @Path("schedule")
    @Secure
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    ScheduledTask create(ScheduledTask schedule){
        ScheduledTask createdSchedule = scheduleService.create(schedule)
        scheduleService.enqueue(createdSchedule)
        return createdSchedule
    }

    @DELETE
    @Secure
    @Path("schedule/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    boolean delete(@PathParam("name") String name){
        scheduleService.delete(name)
    }


}
