package com.trevorism.gcloud.webapi.controller

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
class RootController {

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    String ping(){
        "pong"
    }

    @GET
    @Path("")
    String getEndpoints(){
        '<a href="/ping">/ping</a> </br> <a href="/help">/help</a> </br> <a href="/api/schedule">/api/schedule</a>'
    }

    @GET
    @Path("help")
    String help(){
        return """
<h3>API documentation for schedule </h3><br/><br/>
HTTP GET <a href="/ping">/ping</a> -- Returns "pong" if the application is working
HTTP GET <a href="/api/schedule">/api/schedule</a> -- Lists all the scheduled tasks
HTTP GET <a href="/api/schedule/{name}">/api/schedule/{name}</a> -- Get scheduled task details
HTTP POST <a href="/api/schedule/">/ping</a> -- Create a new scheduled task
HTTP DELETE <a href="/api/schedule/{name}">/api/schedule/{name}</a> -- Delete a scheduled task
HTTP POST <a href="/_ah/queue/default">/_ah/queue/default</a> -- Perform the work of a scheduled task
"""
    }
}
