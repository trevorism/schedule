package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.http.util.CleanUrl

/**
 * @author tbrooks
 */
class ScheduledTaskValidator {

    private final ScheduleService service

    ScheduledTaskValidator(ScheduleService service){
        this.service = service
    }

    static ScheduledTask cleanup(ScheduledTask task, String tenantId){
        task.tenantId = tenantId
        task.name = task.name?.toLowerCase()
        task.httpMethod = task.httpMethod?.toLowerCase()
        if(task.endpoint)
            task.endpoint = CleanUrl.startWithHttps(task.endpoint)

        return task
    }

    void validate(ScheduledTask scheduledTask, boolean allowDuplicate) {
        try{
            (!scheduledTask.id) ?: Integer.parseInt(scheduledTask.id)
            if(!scheduledTask.name)
                throw new RuntimeException("Scheduled task must have a name")
            if(!allowDuplicate && service.getByName(scheduledTask.name)){
                throw new RuntimeException("Scheduled task already exists")
            }
            if(!(["get","post","put","delete"].contains(scheduledTask.httpMethod)))
                throw new RuntimeException("Invalid http method")
            if(!scheduledTask.endpoint)
                throw new RuntimeException("Endpoint not specified")

        }catch (Exception e){
            throw new RuntimeException("Error creating schedule: ${e.message}")
        }
    }
}
