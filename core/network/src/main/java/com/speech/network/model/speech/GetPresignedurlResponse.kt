package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class GetPresignedUrlResponse(
    val status: String,
    val resultCode: Int,
    val data : PresignedUrlData
)

@Serializable
data class PresignedUrlData(
    val url: String,
    val key: String,
)
