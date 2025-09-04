package com.speech.domain.model.speech

import java.util.Locale
import java.util.concurrent.TimeUnit

data class SpeechFeed(
    val id: Int,
    val date: String,
    val fileLength: Long,
    val fileUrl: String,
    val speechFileType: SpeechFileType,
    val speechConfig: SpeechConfig,
) {
    val duration: String
        get() {
            val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(fileLength)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.US, "%d분 %d초", minutes, seconds)
        }
}

