package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.Test

import javax.ws.rs.BadRequestException

/**
 * @author tbrooks
 */
class ScheduledTaskValidatorTest {

    @Test
    void testCleanup() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())
        def task = new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2", httpMethod: "GET")

        def result = validator.cleanup(task)

        assert result.httpMethod == "get"
        assert result.endpoint == "http://ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2"
    }

    @Test
    void testValidate() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTask())
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(id: "123", name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2", httpMethod: "get")

        validator.validate(task)
    }

    @Test(expected = BadRequestException)
    void testValidate_InvalidMatchingName() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTask())
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2", httpMethod: "get")

        validator.validate(task)
    }

    @Test(expected = BadRequestException)
    void testValidate_InvalidHttpMethod() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTask())
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2", httpMethod: "execute")

        validator.validate(task)
    }

    @Test(expected = BadRequestException)
    void testValidate_InvalidEndpoint() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTask())
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}", httpMethod: "get")

        validator.validate(task)
    }

    @Test(expected = BadRequestException)
    void testValidate_InvalidId() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTask())
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(id:"test1", name: "test1", type: "minute", startDate: new Date(), enabled: false,
                requestJson: "{}", endpoint: "ixips-waiter-eastus2-prod.azurewebsites.net/api/waiter/2")

        validator.validate(task)
    }
}
