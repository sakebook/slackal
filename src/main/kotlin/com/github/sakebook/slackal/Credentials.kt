package com.github.sakebook.slackal

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*

val APPLICATION_NAME = "Slackal"
val JSON_FACTORY = JacksonFactory.getDefaultInstance()

val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
val CREDENTIALS_FILE_PATH = "/credentials.json"
val TOKENS_DIRECTORY_PATH = "tokens"

class Credentials {

    fun cal() {
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val service = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCrednetials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
        val cals = service.CalendarList().list()
        cals.forEach {
            println("cals: ${it.key}, ${it.value}")
        }

        val now = DateTime(System.currentTimeMillis())
        val events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items = events.items
        if (items.isEmpty()) {
            println("No upcoming events found.")
        } else {
            println("Upcoming events")
            for (event in items) {
                var start = event.start.dateTime
                if (start == null) {
                    start = event.start.date
                }
                System.out.printf("%s (%s)\n", event.summary, start)
            }
        }
    }
    private fun getCrednetials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val `in` = this::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder()
            .setPort(8888)
            .build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")


    }
}