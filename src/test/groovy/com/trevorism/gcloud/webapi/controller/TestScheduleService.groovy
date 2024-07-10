package com.trevorism.gcloud.webapi.controller

import com.trevorism.data.Repository
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.ScheduleService

import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * @author tbrooks
 */
class TestScheduleService implements ScheduleService {

    int enqueueCalled
    List<ScheduledTask> tasks = []

    @Override
    ScheduledTask create(ScheduledTask schedule, String tenantId) {
        schedule.tenantId = tenantId
        tasks << schedule
        return schedule
    }

    @Override
    ScheduledTask get(String id) {
        tasks.find{
            it.id == id
        }
    }

    @Override
    ScheduledTask update(String id, ScheduledTask scheduledTask) {
        tasks.remove(get(id))
        tasks.add(scheduledTask)
        return scheduledTask
    }

    @Override
    List<ScheduledTask> list() {
        tasks
    }

    @Override
    ScheduledTask delete(String id) {
        ScheduledTask task = get(id)
        tasks.remove(task)
        return task
    }

    @Override
    void enqueue(ScheduledTask schedule, Repository<ScheduledTask> repository) {
        ++enqueueCalled
    }

    @Override
    boolean enqueueAll() {
        return false
    }

    @Override
    boolean cleanup() {
        return false
    }

    static ScheduledTask createTestScheduledTaskNow() {
        new ScheduledTask(id:"123", name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }

    static ScheduledTask createTestScheduledEarlier() {
        new ScheduledTask(id:"123", name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60*30).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }

    static ScheduledTask createTestScheduledLater() {
        new ScheduledTask(id:"123", name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(30).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }
}
