package com.speech.network.model.speech

import android.annotation.SuppressLint
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class GetSpeechConfigResponse(
    val presentationContext: String,
    val audience: String,
    val location: String,
) {
    fun toDomain(): SpeechDetail =
        SpeechDetail(
            speechConfig = SpeechConfig(
                speechType = SpeechType.fromString(presentationContext),
                audience = Audience.fromString(audience),
                venue = Venue.fromString(location),
            )
        )
}
