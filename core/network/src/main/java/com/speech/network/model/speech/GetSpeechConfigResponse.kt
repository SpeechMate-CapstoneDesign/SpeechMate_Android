package com.speech.network.model.speech

import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.Script
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import com.speech.domain.model.speech.VerbalAnalysis
import com.speech.network.model.cursor.Cursor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.Int
import kotlin.io.path.fileVisitor

@Serializable
data class GetSpeechConfigResponse(
    val fileType: String,
    val fileUrl: String,
    val createdAt: LocalDateTime,
    val presentationContext: String,
    val audience: String,
    val location: String,
) {
    fun toDomain(): SpeechDetail = SpeechDetail(
        createdAt = createdAt.toJavaLocalDateTime(),
        fileUrl = fileUrl,
        speechFileType = SpeechFileType.fromString(fileType),
        speechConfig = SpeechConfig(
            speechType = SpeechType.fromString(presentationContext),
            audience = Audience.fromString(audience),
            venue = Venue.fromString(location),
        ),
    )
}
