package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.gcloud.webapi.controller.TestScheduleService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class ScheduledTaskValidatorTest {

    @Test
    void testCleanup() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())
        def task = new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "GET")

        def result = validator.cleanup(task, null)

        assert result.httpMethod == "get"
        assert result.endpoint == "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json"
    }

    @Test
    void testValidate() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())

        def task = new ScheduledTask(id: "123", name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        validator.validate(task, false)
    }

    @Test
    void testValidate_InvalidNullName() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())

        def task = new ScheduledTask(type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        Assertions.assertThrows(RuntimeException, () -> validator.validate(task, false))
    }


    @Test
    void testValidate_InvalidMatchingName() {
        def service = new TestScheduleService()
        service.create(TestScheduleService.createTestScheduledTaskNow(), null)
        ScheduledTaskValidator validator = new ScheduledTaskValidator(service)

        def task = new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        Assertions.assertThrows(RuntimeException, () -> validator.validate(task, false))
    }

    @Test
    void testValidate_InvalidHttpMethod() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "execute")

        Assertions.assertThrows(RuntimeException, () -> validator.validate(task, false))
    }

    @Test
    void testValidate_InvalidEndpoint() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}", httpMethod: "get")

        Assertions.assertThrows(RuntimeException, () ->validator.validate(task, false))
    }

    @Test
    void testValidate_InvalidId() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator(new TestScheduleService())

        def task = new ScheduledTask(id:"test1", name: "test1", type: "minute", startDate: new Date(), enabled: false,
                requestJson: "{}", endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json")

        Assertions.assertThrows(RuntimeException, () ->validator.validate(task, false))
    }
}
