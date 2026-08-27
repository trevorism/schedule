package com.trevorism

import com.google.gson.Gson
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import com.trevorism.https.AppClientSecureHttpClient
import com.trevorism.https.SecureHttpClient

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ScheduleWorld {

    static final String BASE_URL = System.getenv("ACCEPTANCE_BASE_URL") ?: "https://schedule.action.trevorism.com"
    static final String HARMLESS_ENDPOINT = "https://schedule.action.trevorism.com/ping"

    private static final DateTimeFormatter UTC_SECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    private final Gson gson = new Gson()
    private final SecureHttpClient authClient = new AppClientSecureHttpClient()
    private final HttpClient anonClient = new JsonHttpClient()

    final List<String> createdIds = []
    final String taskName = "acceptance-${UUID.randomUUID().toString().take(8)}"

    String body
    boolean rejected
    Map lastTask
    String createdTenantId

    static String minutesFromNow(long minutes) {
        UTC_SECONDS.format(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minutes))
    }

    Map dormantTask(Map overrides = [:]) {
        Map task = [name       : taskName,
                    type       : "daily",
                    httpMethod : "get",
                    endpoint   : HARMLESS_ENDPOINT,
                    requestJson: "{}",
                    enabled    : false,
                    startDate  : minutesFromNow(180)]
        task.putAll(overrides)
        return task
    }

    Map createTask(Map task) {
        body = authClient.post("${BASE_URL}/api/schedule".toString(), gson.toJson(task))
        lastTask = gson.fromJson(body, Map)
        createdTenantId = lastTask?.tenantId
        if (lastTask?.id) {
            createdIds << (lastTask.id as String)
        }
        return lastTask
    }

    void attemptCreateTask(Map task) {
        try {
            createTask(task)
            rejected = false
        }
        catch (Exception ignored) {
            rejected = true
            body = null
        }
    }

    Map fetchById(String id) {
        body = authClient.get("${BASE_URL}/api/schedule/${id}".toString())
        return gson.fromJson(body, Map)
    }

    boolean retrievable(String id) {
        try {
            return fetchById(id)?.id as boolean
        }
        catch (Exception ignored) {
            return false
        }
    }

    List<Map> listTasks() {
        body = authClient.get("${BASE_URL}/api/schedule".toString())
        return gson.fromJson(body, List) ?: []
    }

    Map updateTask(String id, Map task) {
        body = authClient.put("${BASE_URL}/api/schedule/${id}".toString(), gson.toJson(task))
        lastTask = gson.fromJson(body, Map)
        return lastTask
    }

    Map deleteTask(String id) {
        body = authClient.delete("${BASE_URL}/api/schedule/${id}".toString())
        createdIds.remove(id)
        return gson.fromJson(body, Map)
    }

    void anonymously(Closure call) {
        try {
            body = call()
            rejected = false
        }
        catch (Exception ignored) {
            rejected = true
            body = null
        }
    }

    void anonGet(String path) {
        anonymously { anonClient.get("${BASE_URL}/${path}".toString()) }
    }

    void anonPost(String path) {
        anonymously { anonClient.post("${BASE_URL}/${path}".toString(), gson.toJson(dormantTask())) }
    }

    void anonPut(String path) {
        anonymously { anonClient.put("${BASE_URL}/${path}".toString(), gson.toJson(dormantTask())) }
    }

    void anonDelete(String path) {
        anonymously { anonClient.delete("${BASE_URL}/${path}".toString()) }
    }

    void cleanup() {
        createdIds.each { String id ->
            try {
                authClient.delete("${BASE_URL}/api/schedule/${id}".toString())
            }
            catch (Exception ignored) {
            }
        }
        createdIds.clear()
    }
}
