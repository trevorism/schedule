package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService
import com.trevorism.gcloud.service.type.ImmediateScheduleType
import com.trevorism.gcloud.service.type.ScheduleType
import com.trevorism.gcloud.service.type.ScheduleTypeFactory
import com.trevorism.http.headers.HeadersHttpClient
import com.trevorism.http.headers.HeadersJsonHttpClient
import com.trevorism.secure.PasswordProvider

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
    private HeadersHttpClient client = new HeadersJsonHttpClient()
    private PasswordProvider provider = new PasswordProvider()

    @POST
    @Path("default")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    boolean performAction(ScheduledTask task){
        String name = task.name
        ScheduledTask schedule = scheduleService.getByName(name)
        if(schedule?.enabled){
            executeAndEnqueueTask(name, schedule)
        }
        else{
            logDidNotExecuteSchedule(schedule, name)
        }

        return schedule?.enabled

    }

    private void executeAndEnqueueTask(String name, ScheduledTask schedule) {
        log.info("Performing work on scheduled task: ${name}")
        client."${schedule.httpMethod.toLowerCase()}"(schedule.endpoint, schedule.requestJson, ["Authorization": provider.password])
        ScheduleType type = ScheduleTypeFactory.create(schedule.type)
        if (!(type instanceof ImmediateScheduleType)) {
            log.info("Enqueuing the next run for task ${name}")
            scheduleService.enqueue(schedule)
        }
    }

    private static void logDidNotExecuteSchedule(ScheduledTask schedule, String name) {
        if (!schedule)
            log.warning("Scheduled task ${name} not found")
        else
            log.info("Scheduled task ${name} is disabled")
    }

}
