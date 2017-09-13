package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService

import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import java.util.logging.Logger

/**
 * @author tbrooks
 */
@Path("/_ah/queue")
class WorkerController {

    private static final Logger log = Logger.getLogger(WorkerController.class.name)

    private ScheduleService scheduleService = new DefaultScheduleService()

    @POST
    @Path("default")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    boolean performAction(String name){
        log.info("Performing work on scheduled task: ${name}")
        ScheduledTask schedule = scheduleService.getByName(name)
        if(schedule?.enabled){
            //do scheduled work
            scheduleService.enqueue(schedule)
        }
        else{
            if(!schedule)
                log.warning("Scheduled task ${name} not found")
            else
                log.info("Scheduled task ${name} is disabled")
        }

        return schedule?.enabled

    }

}
