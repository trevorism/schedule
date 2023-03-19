package com.trevorism.gcloud.webapi.controller

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
    ScheduledTask create(ScheduledTask schedule) {
        tasks << schedule
        return schedule
    }

    @Override
    ScheduledTask getByName(String name) {
        tasks.find{
            it.name == name
        }
    }

    @Override
    ScheduledTask update(ScheduledTask scheduledTask, String name) {
        tasks.remove(getByName(name))
        tasks.add(scheduledTask)
        return scheduledTask
    }

    @Override
    List<ScheduledTask> list() {
        tasks
    }

    @Override
    boolean delete(String name) {
        tasks.remove(getByName(name))
    }

    @Override
    void enqueue(ScheduledTask schedule) {
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
        new ScheduledTask(name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }

    static ScheduledTask createTestScheduledEarlier() {
        new ScheduledTask(name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).minusSeconds(60*30).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }

    static ScheduledTask createTestScheduledLater() {
        new ScheduledTask(name: "test", type: "minute", startDate: Date.from(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(30).toInstant()), enabled: false, requestJson: "{}",
                endpoint:"https://endpoint-tester.testing.trevorism.com/api/json")
    }
}
