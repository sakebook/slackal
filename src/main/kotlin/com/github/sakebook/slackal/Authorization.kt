package com.github.sakebook.slackal

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.Preconditions
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.CalendarScopes
import java.awt.Desktop
import java.io.*
import java.net.URI
import java.util.*
import java.util.logging.Level

class Authorization {

    val SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY)
    val CREDENTIALS_FILE_PATH = "/credentials/client_secret.json"
    val ENV_CLIENT_SECRET = "CLIENT_SECRET"
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
            .setCallbackPath("/oauth2callback")
            .build()
        return AuthorizationCodeInstalledApp(flow, receiver, CustomBrowser()).authorize("user")
    }

    private fun getReader(): Reader {
        val clientSecret = System.getenv(ENV_CLIENT_SECRET)
        return if (clientSecret.isNullOrBlank()) {
            val inputStream = this::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            InputStreamReader(inputStream)
        } else {
            StringReader(clientSecret)
        }
    }
}

class CustomBrowser : AuthorizationCodeInstalledApp.Browser {

    @Throws(IOException::class)
    override fun browse(url: String) {
        customBrowse(url)
//        AuthorizationCodeInstalledApp.browse(url)
    }

    fun customBrowse(url: String) {
        Preconditions.checkNotNull(url)
        // Ask user to open in their browser using copy-paste
        println("Please open the following address in your browser:")
        println("  $url")
        // Attempt to open it in the browser
        try {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    println("Attempting to open that address in the default browser now...")
                    desktop.browse(URI.create(url))
                } else {
                    println("desktop.isSupported(Desktop.Action.BROWSE) is false")
                }
            } else {
                println("Desktop.isDesktopSupported() is false")
            }
        } catch (e: IOException) {
            println("Unable to open browser, $e")
//            LOGGER.log(Level.WARNING, "Unable to open browser", e)
        } catch (e: InternalError) {
            // A bug in a JRE can cause Desktop.isDesktopSupported() to throw an
            // InternalError rather than returning false. The error reads,
            // "Can't connect to X11 window server using ':0.0' as the value of the
            // DISPLAY variable." The exact error message may vary slightly.
            println("Unable to open browser, $e")
//            LOGGER.log(Level.WARNING, "Unable to open browser", e)
        }
    }
}
