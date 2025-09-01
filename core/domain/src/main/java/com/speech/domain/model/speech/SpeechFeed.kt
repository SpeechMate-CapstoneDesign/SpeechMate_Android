package com.speech.domain.model.speech

data class SpeechFeed(
    val fileName: String,
    val date: String,
    val fileLength: Long,
    val speechFileType: SpeechFileType,
    val speechConfig: SpeechConfig,
)
