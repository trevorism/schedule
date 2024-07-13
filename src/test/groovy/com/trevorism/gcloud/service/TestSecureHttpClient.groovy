package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trevorism.gcloud.schedule.model.ScheduledTask
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.https.SecureHttpClient
import com.trevorism.https.token.ObtainTokenStrategy

class TestSecureHttpClient implements SecureHttpClient{

    private Gson gson = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").create()
    List<ScheduledTask> tasks = []

    @Override
    ObtainTokenStrategy getObtainTokenStrategy() {
        return null
    }

    @Override
    HttpClient getHttpClient() {
        return null
    }

    @Override
    String get(String s) {
        if(s == "https://datastore.data.trevorism.com/object/scheduledtask" || s == "https://datastore.data.trevorism.com/all/scheduledtask")
            return gson.toJson(tasks)
        if(tasks)
            return gson.toJson(tasks[0])
        return "{}"
    }

    @Override
    HeadersHttpResponse get(String s, Map<String, String> map) {
        if(s == "https://datastore.data.trevorism.com/object/scheduledtask/123")
            return new HeadersHttpResponse("{}", map)
        if(s == "https://datastore.data.trevorism.com/object/scheduledtask/456")
            return new HeadersHttpResponse("{}", map)
        return new HeadersHttpResponse(gson.toJson(tasks), map)
    }

    @Override
    String post(String s, String s1) {
        if(s == "https://datastore.data.trevorism.com/object/scheduledtask"){
            ScheduledTask st = gson.fromJson(s1, ScheduledTask)
            tasks << st
            return gson.toJson(st)
        }
        return s1
    }

    @Override
    HeadersHttpResponse post(String s, String s1, Map<String, String> map) {
        return new HeadersHttpResponse(s1, map)
    }

    @Override
    String put(String s, String s1) {
        tasks.remove(0)
        ScheduledTask st = gson.fromJson(s1, ScheduledTask)
        tasks << st
        return gson.toJson(st)
    }

    @Override
    HeadersHttpResponse put(String s, String s1, Map<String, String> map) {
        return null
    }

    @Override
    String patch(String s, String s1) {
        return null
    }

    @Override
    HeadersHttpResponse patch(String s, String s1, Map<String, String> map) {
        return null
    }

    @Override
    String delete(String s) {
        tasks.remove(0)
        return "{}"
    }

    @Override
    HeadersHttpResponse delete(String s, Map<String, String> map) {
        return new HeadersHttpResponse("{}", map)
    }
}
