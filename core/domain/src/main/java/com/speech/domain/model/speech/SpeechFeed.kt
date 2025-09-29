package com.speech.domain.model.speech

import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

data class SpeechFeed(
    val id: Int,
    val date: String,
    val fileLength: Duration,
    val fileUrl: String,
    val speechFileType: SpeechFileType,
    val speechConfig: SpeechConfig,
) {
    val duration: String by lazy {
        fileLength.toComponents { minutes, seconds, _ ->
            "${minutes}분 ${seconds}초"
        }
    }
}
