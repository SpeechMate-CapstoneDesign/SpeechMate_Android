package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSpeechConfigRequest(
    val title: String,
    val presentationContext: String,
    val audience: String,
    val location: String,
)
