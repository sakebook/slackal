package com.github.sakebook.slackal

import com.google.api.services.calendar.model.Event
import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.apache.commons.codec.digest.DigestUtils

class Messaging {

    private val df: SimpleDateFormat = SimpleDateFormat("HH:mm").apply {
        timeZone = TimeZone.getTimeZone("Asia/Tokyo")
    }

    fun createJSON(events: List<Event>): String {
        if (events.isEmpty()) {
            return Json.stringify(Message.serializer(), Message(text = "直近の予定はありません"))
        }

        val attachments = events.map {
            Attachment(
                text = "${df.format(it.start.dateTime.value)}~${df.format(it.end.dateTime.value)}",
                fields = listOf(
                    Field(true, "場所", it.location),
                    Field(true, "作成者", it.creator.email.substringBefore("@"))
                ),
                color = createColorFromName(it.location),
                title_link = it.htmlLink,
                title = it.summary,
                footer = it.attendees.fold("参加者") { s1, s2 -> "$s1 ${s2.email.substringBefore("@")}" }
            )
        }
        val message = Message(
            attachments,
            text = "直近の予定"
        )
        return Json.stringify(Message.serializer(), message)
    }

    private fun createColorFromName(input: String): String {
        val digest = DigestUtils.md5Hex(input)
        val r = Integer.valueOf(digest.substring(1, 3), 16)
        val g = Integer.valueOf(digest.substring(3, 5), 16)
        val b = Integer.valueOf(digest.substring(5, 7), 16)
        val rgb = "$r$b$g"
        return "#" + rgb.substring(1, 7)
    }
}

@Serializable
data class Message(
    @Optional
    val attachments: List<Attachment>? = null,
    @Optional
    val channel: String? = null,
    @Optional
    val icon_emoji: String? = null,
    val text: String,
    @Optional
    val username: String? = null
)

@Serializable
data class Attachment(
    @Optional
    val author_icon: String? = null,
    @Optional
    val author_link: String? = null,
    @Optional
    val author_name: String? = null,
    @Optional
    val color: String? = null,
    @Optional
    val fallback: String? = null,
    @Optional
    val fields: List<Field>? = null,
    @Optional
    val footer: String? = null,
    @Optional
    val footer_icon: String? = null,
    @Optional
    val image_url: String? = null,
    @Optional
    val pretext: String? = null,
    @Optional
    val text: String? = null,
    @Optional
    val thumb_url: String? = null,
    @Optional
    val title: String? = null,
    @Optional
    val title_link: String? = null,
    @Optional
    val ts: Int? = null
)

@Serializable
data class Field(
    @Optional
    val short: Boolean? = null,
    @Optional
    val title: String? = null,
    @Optional
    val value: String? = null
)