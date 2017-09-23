package com.trevorism.gcloud.webapi.controller.inject

import com.trevorism.data.PingingDatastoreRepository
import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.ScheduleService

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

/**
 * @author tbrooks
 */
@WebListener
class EnqueueScheduledTasks implements ServletContextListener {

    @Override
    void contextInitialized(ServletContextEvent sce) {
        ScheduleService service = new DefaultScheduleService()
        Repository<ScheduledTask> repo = new PingingDatastoreRepository<>(ScheduledTask.class)
        repo.list().each { ScheduledTask st ->
            service.enqueue(st)
        }
    }

    @Override
    void contextDestroyed(ServletContextEvent sce) {

    }
}
