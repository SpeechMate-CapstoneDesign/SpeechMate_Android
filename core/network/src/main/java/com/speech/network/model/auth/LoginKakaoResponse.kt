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
    val access: String,
    val refresh: String,
    val accessExpiredAt: String,
    val refreshExpiredAt: String,
    val newUser: Boolean
)