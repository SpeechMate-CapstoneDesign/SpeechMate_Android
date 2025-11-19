package com.speech.network.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class PostDeviceTokenRequest(
    val token : String
)
