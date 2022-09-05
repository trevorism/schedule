package com.trevorism.gcloud.service.type

/**
 * @author tbrooks
 */
class ScheduleTypeFactory {


    public static final String WEEKLY = "weekly"
    public static final String DAILY = "daily"
    public static final String HOURLY = "hourly"
    public static final String IMMEDIATE = "immediate"

    private ScheduleTypeFactory(){}

    static ScheduleType create(String taskType){
        if(WEEKLY == taskType)
            return new WeeklyScheduleType()
        if(DAILY == taskType)
            return new DailyScheduleType()
        if(HOURLY == taskType)
            return new HourlyScheduleType()

        return new ImmediateScheduleType()
    }
}
