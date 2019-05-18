package com.github.sakebook.slackal


import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val port = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port.toInt()) {
        routing {
            get("/") {
                println("/")
                args.forEach {
                    println("$it")
                }
                val json = """
{
    "text": "sample text",
    "attachments": [
        {
            "text":"sample attachment text"
        }
    ]
}
                """.trimIndent()

                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}