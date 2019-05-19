package com.github.sakebook.slackal

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    println("Running...")
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port.toInt()) {
        routing {
            get("/") {
                println("Access path '/'")
                val eventList = CalendarClient.getEventList()
                val messaging = Messaging()
                val json = messaging.createJSON(eventList)
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}