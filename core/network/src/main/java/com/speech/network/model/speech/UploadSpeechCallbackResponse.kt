package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class UploadSpeechCallbackResponse(
    val status: String,
    val resultCode: Int,
    val data: SpeechData
)

@Serializable
data class SpeechData(
    val speechId: Int
)