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
import java.io.*
import java.util.*

class Authorization {

    val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
    val CREDENTIALS_FILE_PATH = "/credentials/client_secret.json"
    val TOKENS_DIRECTORY_PATH = "tokens"

    fun getCredential(
        HTTP_TRANSPORT: NetHttpTransport,
        JSON_FACTORY: JacksonFactory
    ): Credential {
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, getReader())

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder()
            .setPort(5000)
            .build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    private fun getReader(): Reader {
        val clientSecret = System.getenv("CLIENT_SECRET")
        return if (clientSecret.isNullOrBlank()) {
            val inputStream = this::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            InputStreamReader(inputStream)
        } else {
            StringReader(clientSecret)
        }
    }
}