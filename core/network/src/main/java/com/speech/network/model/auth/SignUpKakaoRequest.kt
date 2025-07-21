package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignUpKakaoRequest(
    val idToken: String,
    val onBoardingDto: OnBoardingDto
)

@Serializable
data class OnBoardingDto(
    val skill: String
)
