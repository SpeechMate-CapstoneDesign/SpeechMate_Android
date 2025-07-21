package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class ReissueTokenResponse(
    val status: String,
    val resultCode: Int,
    val data: ReissueTokenData,
)

@Serializable
data class ReissueTokenData(
    val access: String,
    val refresh: String,
    val accessExpiredAt: String,
    val refreshExpiredAt: String,
)
