package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginKakaoResponse(
    val userId : Int? = null,
    val access: String? = null,
    val refresh: String? = null,
    val accessExpiredAt: String? = null,
    val refreshExpiredAt: String? = null,
    val newUser: Boolean
)
