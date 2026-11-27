package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.service.DefaultScheduleService
import com.trevorism.gcloud.service.TestSecureHttpClient
import com.trevorism.https.SecureHttpClient
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.security.authentication.Authentication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

/**
 * @author tbrooks
 */
class ScheduleControllerTest {

    ScheduleController scheduleController = new ScheduleController()

    @BeforeEach
    void setup(){
        scheduleController.scheduleService = new TestScheduleService()
        scheduleController.create(TestScheduleService.createTestScheduledTaskNow(), { } as Authentication)
    }

    @Test
    void testList() {
        def list = scheduleController.list()
        assert list
        assert list.size() == 1
        assert list[0].name == "test"
        assert list[0].type == "minute"
        assert !list[0].enabled

    }

    @Test
    void testGet() {
        ScheduledTask task = scheduleController.get("123")
        assert task.id == "123"
        assert task.type == "minute"
        assert !task.enabled

        assert !scheduleController.get("blah")
        assert !scheduleController.get(null)

    }

    @Test
    void testCreate() {
        ScheduledTask task = new ScheduledTask(id:"22", name: "test2", type: "minute", startDate: new Date(), enabled: true)
        scheduleController.create(task, null)

        assert scheduleController.list().size() == 2
        scheduleController.delete("22", null)
        assert scheduleController.list().size() == 1

    }

    @Test
    void testDelete() {
        scheduleController.delete("123", null)
        assert scheduleController.list().size() == 0
    }

    @Test
    void testUpdate() {
        ScheduledTask task = new ScheduledTask(id: "123", name: "test", type: "progressive", startDate: new Date(), enabled: true)
        scheduleController.update("123", task)
        assert scheduleController.list().size() == 1
        assert scheduleController.get("123").enabled
        assert scheduleController.get("123").type == "progressive"

    }

    @Test
    void testCreateInvalidTaskIsBadRequest() {
        scheduleController.scheduleService = new DefaultScheduleService()
        ScheduledTask task = new ScheduledTask(name: "test", httpMethod: "execute", endpoint: "https://example.com")
        assertStatus(HttpStatus.BAD_REQUEST, () -> scheduleController.create(task, null))
    }

    @Test
    void testUpdateInvalidTaskIsBadRequest() {
        scheduleController.scheduleService = new DefaultScheduleService()
        ScheduledTask task = new ScheduledTask(httpMethod: "get", endpoint: "https://example.com")
        assertStatus(HttpStatus.BAD_REQUEST, () -> scheduleController.update("123", task))
    }

    @Test
    void testGetMissingIdIsNotFound() {
        scheduleController.scheduleService = serviceWithEmptyDatastore()
        assertStatus(HttpStatus.NOT_FOUND, () -> scheduleController.get("404"))
    }

    @Test
    void testUpdateMissingIdIsNotFound() {
        scheduleController.scheduleService = serviceWithEmptyDatastore()
        assertStatus(HttpStatus.NOT_FOUND, () -> scheduleController.update("404", TestScheduleService.createTestScheduledTaskNow()))
    }

    @Test
    void testDeleteMissingIdIsNotFound() {
        scheduleController.scheduleService = serviceWithEmptyDatastore()
        assertStatus(HttpStatus.NOT_FOUND, () -> scheduleController.delete("404", null))
    }

    private static DefaultScheduleService serviceWithEmptyDatastore() {
        SecureHttpClient datastore = TestSecureHttpClient.respondingWith(HttpStatus.NOT_FOUND.code)
        DefaultScheduleService service = new DefaultScheduleService()
        service.secureHttpClient = datastore
        service.singletonClient = datastore
        return service
    }

    private static void assertStatus(HttpStatus expected, Executable call) {
        HttpStatusException e = Assertions.assertThrows(HttpStatusException, call)
        assert e.status == expected
    }
}
