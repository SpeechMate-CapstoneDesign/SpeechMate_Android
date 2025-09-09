package com.speech.network.model.speech

import android.annotation.SuppressLint
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import com.speech.network.model.cursor.Cursor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class GetSpeechFeedResponse(
    @SerialName("speeches") val speechFeeds: List<SpeechFeedResult>,
    val hasNext: Boolean,
    @SerialName("cursordto") val cursor: Cursor<Int>?,
) {
    fun toDomain(): List<SpeechFeed> = speechFeeds.map { it.toDomain() }
}

@Serializable
data class SpeechFeedResult(
    val id: Int,
    val createdAt: LocalDateTime,
    val duration : Long,
    val fileType: String,
    val fileUrl: String,
    val title: String,
    val presentationContext: String,
    val audience: String,
    val location: String,
) {
    fun toDomain(): SpeechFeed = SpeechFeed(
        id = id,
        date = date,
        fileLength = duration,
        fileUrl = fileUrl,
        speechFileType = SpeechFileType.fromString(fileType),
        speechConfig = SpeechConfig(
            fileName = title,
            speechType = SpeechType.fromString(presentationContext),
            audience = Audience.fromString(audience),
            venue = Venue.fromString(location),
        ),
    )
    val date: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("yy.MM.dd")
            return createdAt.toJavaLocalDateTime().format(formatter)
        }
}
