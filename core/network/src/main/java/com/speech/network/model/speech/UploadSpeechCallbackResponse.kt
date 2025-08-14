package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class UploadSpeechCallbackResponse(
    val fileKey: String
)
