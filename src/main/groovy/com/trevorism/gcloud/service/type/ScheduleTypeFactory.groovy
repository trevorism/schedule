package com.trevorism.gcloud.service.type

/**
 * @author tbrooks
 */
class ScheduleTypeFactory {

    private ScheduleTypeFactory(){}

    static ScheduleType create(String name){
        if("daily" == name)
            return new DailyScheduleType()
        if("hourly" == name)
            return new HourlyScheduleType()
        if("minute" == name)
            return new MinuteScheduleType()

        return new ImmediateScheduleType()
    }
}
