package com.trevorism.gcloud

import com.trevorism.ScheduleWorld

this.metaClass.mixin(io.cucumber.groovy.Hooks)
this.metaClass.mixin(io.cucumber.groovy.EN)

World {
    new ScheduleWorld()
}

After { ->
    cleanup()
}

Given(~/^a dormant scheduled task exists$/) { ->
    createTask(dormantTask())
}

Given(~/^an immediate task starting now$/) { ->
    createTask(dormantTask([type: "immediate", enabled: true, startDate: ScheduleWorld.minutesFromNow(0)]))
}

Given(~/^an immediate task starting in (\d+) minutes$/) { Integer minutes ->
    createTask(dormantTask([type: "immediate", enabled: true, startDate: ScheduleWorld.minutesFromNow(minutes)]))
}

When(~/^a task is created without a name$/) { ->
    Map task = dormantTask()
    task.remove("name")
    attemptCreateTask(task)
}

When(~/^a task is created without an endpoint$/) { ->
    Map task = dormantTask()
    task.remove("endpoint")
    attemptCreateTask(task)
}

When(~/^a task is created without an http method$/) { ->
    Map task = dormantTask()
    task.remove("httpMethod")
    attemptCreateTask(task)
}

When(~/^a task is created with the http method "(.*)"$/) { String httpMethod ->
    attemptCreateTask(dormantTask([httpMethod: httpMethod]))
}

When(~/^a task is created with the name "(.*)"$/) { String name ->
    attemptCreateTask(dormantTask([name: name]))
}

When(~/^a task is created with the endpoint "(.*)"$/) { String endpoint ->
    attemptCreateTask(dormantTask([endpoint: endpoint]))
}

When(~/^the task type is updated to "(.*)"$/) { String type ->
    Map current = fetchById(lastTask.id as String)
    current.type = type
    updateTask(lastTask.id as String, current)
}

When(~/^the task is updated with a tenantId of "(.*)"$/) { String tenantId ->
    Map current = fetchById(lastTask.id as String)
    current.tenantId = tenantId
    updateTask(lastTask.id as String, current)
}

When(~/^the task is deleted$/) { ->
    deleteTask(lastTask.id as String)
}

When(~/^I GET "(.*)" anonymously$/) { String path ->
    anonGet(path)
}

When(~/^I POST "(.*)" anonymously$/) { String path ->
    anonPost(path)
}

When(~/^I PUT "(.*)" anonymously$/) { String path ->
    anonPut(path)
}

When(~/^I DELETE "(.*)" anonymously$/) { String path ->
    anonDelete(path)
}

Then(~/^the task is created successfully$/) { ->
    assert !rejected
    assert lastTask.id
}

Then(~/^the request is rejected$/) { ->
    assert rejected
}

Then(~/^the task can be retrieved by id$/) { ->
    Map found = fetchById(lastTask.id as String)
    assert found.id == lastTask.id
    assert found.name == taskName
}

Then(~/^the task appears in the full list$/) { ->
    assert listTasks().any { it.id == lastTask.id }
}

Then(~/^the task can no longer be retrieved$/) { ->
    assert !retrievable(lastTask.id as String)
}

Then(~/^the stored task type is "(.*)"$/) { String type ->
    assert fetchById(lastTask.id as String).type == type
}

Then(~/^the stored name is "(.*)"$/) { String name ->
    assert !rejected
    assert lastTask.name == name
}

Then(~/^the stored http method is "(.*)"$/) { String httpMethod ->
    assert !rejected
    assert lastTask.httpMethod == httpMethod
}

Then(~/^the stored endpoint is "(.*)"$/) { String endpoint ->
    assert !rejected
    assert lastTask.endpoint == endpoint
}

Then(~/^the stored tenantId is unchanged$/) { ->
    assert fetchById(lastTask.id as String).tenantId == createdTenantId
}

Then(~/^the stored tenantId is not "(.*)"$/) { String tenantId ->
    assert fetchById(lastTask.id as String).tenantId != tenantId
}

Then(~/^the task was enqueued$/) { ->
    assert lastTask.enabled == false
}

Then(~/^the task was not enqueued$/) { ->
    assert lastTask.enabled == true
}

Then(~/^the stored task is disabled$/) { ->
    assert fetchById(lastTask.id as String).enabled == false
}

Then(~/^the response body is "(.*)"$/) { String expected ->
    assert body?.trim() == expected
}
