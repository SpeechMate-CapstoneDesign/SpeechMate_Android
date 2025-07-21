package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpKakaoResponse(
    val status: String,
    val resultCode: Int,
    val data: SignUpKakaoData
)

@Serializable
data class SignUpKakaoData(
    val access: String,
    val refresh: String,
    val accessExpiredAt: String,
    val refreshExpiredAt: String,
    val newUser: Boolean
)