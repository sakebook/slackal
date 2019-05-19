package com.github.sakebook.slackal

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import java.io.FileReader
import java.util.*

object CalendarClient {

    val APPLICATION_NAME = "Slackal"
    val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    val authorization = Authorization()

    private fun getCalendar(): Calendar {
        return Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorization.getCredential(HTTP_TRANSPORT, JSON_FACTORY))
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun getCalenderIds(): List<String> {
        val prop = Properties()
        val propertiesFile = System.getProperty("user.dir") + "/calendars.properties"
        val reader = FileReader(propertiesFile)
        prop.load(reader)
        val calendarsString = prop["calendars"] as String
        return calendarsString.split(",")
    }

    private fun getEvents(calendar: Calendar, calendarId: String, now: DateTime): List<Event> {
        val events = calendar.events().list(calendarId)
            .setMaxResults(4)
            .setTimeMin(now)
            .setTimeMax(DateTime(now.value + 1000L * 60L * 60L * 2L)) // 2時間
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        return events.items
    }

    fun getEventList(): List<Event> {
        val now = DateTime(System.currentTimeMillis())
        val service = getCalendar()
        val ids = getCalenderIds()
        return ids.map {
            getEvents(service, it, now)
        }.flatten()
    }
}