package com.github.sakebook.slackal

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import java.io.FileReader
import java.util.Properties

object CalendarClient {

    val APPLICATION_NAME = "Slackal"
    val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val authorization = Authorization()
    private const val ENV_CALENDAR_IDS = "CALENDAR_IDS"

    private fun getCalendar(): Calendar {
        return Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorization.getCredential(HTTP_TRANSPORT, JSON_FACTORY))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun getCalendarIds(): List<String> {
        val calendarIds = System.getenv(ENV_CALENDAR_IDS)
        return when (calendarIds.isNullOrBlank()) {
            true -> {
                println("Load calendar from local file")
                val prop = Properties()
                val propertiesFile = System.getProperty("user.dir") + "/calendars.properties"
                val reader = FileReader(propertiesFile)
                prop.load(reader)
                prop["calendars"] as String
            }
            false -> {
                println("Load calendar from env")
                calendarIds
            }
        }.split(",")
    }

    private fun getEvents(calendar: Calendar, calendarId: String, now: DateTime): List<Event> {
        return try {
            val events = calendar.events().list(calendarId)
                .setMaxResults(4)
                .setTimeMin(now)
                .setTimeMax(DateTime(now.value + 1000L * 60L * 60L * 2L)) // 2時間
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
            println("events: ${events.items}")
            events.items
        } catch (e: Exception) {
            println("getEvents error: ${e.message}")
            emptyList()
        }
    }

    fun getEventList(): List<Event> {
        val now = DateTime(System.currentTimeMillis())
        val service = getCalendar()
        val ids = getCalendarIds()
        return ids.map {
            getEvents(service, it, now)
        }.flatten()
    }
}