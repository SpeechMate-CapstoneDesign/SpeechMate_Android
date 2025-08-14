package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginKakaoResponse(
    val access: String? = null,
    val refresh: String? = null,
    val accessExpiredAt: String? = null,
    val refreshExpiredAt: String? = null,
    val newUser: Boolean
)