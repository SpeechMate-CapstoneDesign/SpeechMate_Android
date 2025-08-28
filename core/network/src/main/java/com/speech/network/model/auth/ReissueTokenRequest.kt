package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenRequest(
    val refreshToken: String
)
