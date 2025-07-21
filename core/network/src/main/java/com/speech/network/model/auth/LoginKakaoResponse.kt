package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginKakaoResponse(
    val status: String,
    val resultCode: Int,
    val data: LoginKakaoData
)

@Serializable
data class LoginKakaoData(
    val access: String? = null,
    val refresh: String? = null,
    val accessExpiredAt: String? = null,
    val refreshExpiredAt: String? = null,
    val newUser: Boolean
)