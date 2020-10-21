package com.trevorism.gcloud.service.type

/**
 * @author tbrooks
 */
class ScheduleTypeFactory {

    private ScheduleTypeFactory(){}

    static ScheduleType create(String taskType){
        if("daily" == taskType)
            return new DailyScheduleType()
        if("hourly" == taskType)
            return new HourlyScheduleType()

        return new ImmediateScheduleType()
    }
}
