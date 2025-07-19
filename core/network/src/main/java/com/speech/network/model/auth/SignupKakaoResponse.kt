package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignupKakaoResponse(
    val status: String,
    val resultCode: Int,
    val data: SignupKakaoData
)

@Serializable
data class SignupKakaoData(
    val access: String,
    val refresh: String,
    val accessExpiredAt: String,
    val refreshExpiredAt: String,
    val newUser: Boolean
)