package com.trevorism.gcloud.service

import com.trevorism.gcloud.schedule.model.ScheduledTask
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class ScheduledTaskValidatorTest {

    @Test
    void testCleanup() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()
        def task = new ScheduledTask(name: "test", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "GET")

        def result = validator.cleanup(task, null)

        assert result.httpMethod == "get"
        assert result.endpoint == "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json"
    }

    @Test
    void testValidate() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(id: "123", name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        validator.validate(task)
    }

    @Test
    void testValidate_InvalidNullName() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        assertBadRequest(validator, task)
    }

    @Test
    void testValidate_InvalidHttpMethod() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}",
                endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "execute")

        assertBadRequest(validator, task)
    }

    @Test
    void testValidate_InvalidEndpoint() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(name: "test1", type: "minute", startDate: new Date(), enabled: false, requestJson: "{}", httpMethod: "get")

        assertBadRequest(validator, task)
    }

    @Test
    void testValidate_DatastoreGeneratedId() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(id: "4884080256614400", name: "test1", type: "minute", startDate: new Date(), enabled: false,
                requestJson: "{}", endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json", httpMethod: "get")

        validator.validate(task)
    }

    @Test
    void testValidate_InvalidId() {
        ScheduledTaskValidator validator = new ScheduledTaskValidator()

        def task = new ScheduledTask(id:"test1", name: "test1", type: "minute", startDate: new Date(), enabled: false,
                requestJson: "{}", endpoint: "https://endpoint-tester-dot-trevorism-testing.appspot.com/api/json")

        assertBadRequest(validator, task)
    }

    private static void assertBadRequest(ScheduledTaskValidator validator, ScheduledTask task) {
        HttpStatusException e = Assertions.assertThrows(HttpStatusException, () -> validator.validate(task))
        assert e.status == HttpStatus.BAD_REQUEST
    }
}
