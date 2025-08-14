package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpKakaoResponse(
    val access: String,
    val refresh: String,
    val accessExpiredAt: String,
    val refreshExpiredAt: String,
    val newUser: Boolean
)
