package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class GetSpeechToTextResponse(
    val data : String
)