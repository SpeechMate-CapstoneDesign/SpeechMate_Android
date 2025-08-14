 package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class GetPresignedUrlResponse(
    val url: String,
    val key: String,
)

