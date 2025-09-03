package com.speech.network.model.speech

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadSpeechCallbackResponse(
    val speechId: Int,
    @SerialName("s3Url") val fileUrl: String,
)
