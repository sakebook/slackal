package com.github.sakebook.slackal

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.util.*

class Authorization {

    val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
    val CREDENTIALS_FILE_PATH = "/credentials/client_secret.json"
    val TOKENS_DIRECTORY_PATH = "tokens"

    fun getCredential(
        HTTP_TRANSPORT: NetHttpTransport,
        JSON_FACTORY: JacksonFactory
    ): Credential {
        val `in` = this::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: run {
                println("Resource not found: $CREDENTIALS_FILE_PATH")
                throw FileNotFoundException("Resource not found: $CREDENTIALS_FILE_PATH")
            }
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val port = System.getenv("PORT") ?: "8080"
        println("port: $port")
        val receiver = LocalServerReceiver.Builder()
            .setPort(port.toInt())
            .build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}