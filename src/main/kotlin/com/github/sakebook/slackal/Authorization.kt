package com.github.sakebook.slackal

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Collections
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential

class Authorization {

    val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
    val CREDENTIALS_FILE_PATH = "/credentials/client_secret.json"
    val ENV_CLIENT_SECRET = "CLIENT_SECRET"
    val ENV_ACCOUNT_USER = "ACCOUNT_USER"

    fun getCredential(
        HTTP_TRANSPORT: NetHttpTransport,
        JSON_FACTORY: JacksonFactory
    ): Credential {
        return createCredentialFromJson(HTTP_TRANSPORT, JSON_FACTORY)
    }

    private fun getReader(): InputStream {
        val clientSecret = System.getenv(ENV_CLIENT_SECRET)
        return if (clientSecret.isNullOrBlank()) {
            this::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
        } else {
            ByteArrayInputStream(clientSecret.toByteArray(Charsets.UTF_8))
        }
    }

    private fun createCredentialFromJson(
        HTTP_TRANSPORT: NetHttpTransport,
        JSON_FACTORY: JacksonFactory
    ): Credential {
        val credential = GoogleCredential.fromStream(getReader())
        return credential.toBuilder()
            .setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountScopes(SCOPES)
            .setServiceAccountUser(System.getenv(ENV_ACCOUNT_USER))
            .build()
    }
}