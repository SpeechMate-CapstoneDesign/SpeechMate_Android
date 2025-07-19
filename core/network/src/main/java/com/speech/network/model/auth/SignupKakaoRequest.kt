package com.speech.network.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignupKakaoRequest(
    val idToken: String,
    val onBoardingDto: OnBoardingDto
)

@Serializable
data class OnBoardingDto(
    val skill: String
)
