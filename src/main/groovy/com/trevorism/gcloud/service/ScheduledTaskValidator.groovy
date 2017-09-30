package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.http.util.CleanUrl

import javax.ws.rs.BadRequestException

/**
 * @author tbrooks
 */
class ScheduledTaskValidator {

    private final ScheduleService service

    ScheduledTaskValidator(ScheduleService service){
        this.service = service
    }

    static ScheduledTask cleanup(ScheduledTask task){
        task.httpMethod = task.httpMethod?.toLowerCase()
        if(task.endpoint)
            task.endpoint = CleanUrl.startWithHttp(task.endpoint)

        return task
    }

    void validate(ScheduledTask scheduledTask) {
        try{
            (!scheduledTask.id) ?: Integer.parseInt(scheduledTask.id)
            if(service.getByName(scheduledTask.name)){
                throw new Exception("Scheduled task already exists")
            }
            if(!(["get","post","put","delete"].contains(scheduledTask.httpMethod)))
                throw new Exception("Invalid http method")
            if(!scheduledTask.endpoint)
                throw new Exception("Endpoint not specified")

        }catch (Exception e){
            throw new BadRequestException("Error creating schedule", e)
        }
    }
}
