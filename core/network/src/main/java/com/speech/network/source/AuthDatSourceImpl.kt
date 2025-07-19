package com.speech.network.source

import com.speech.network.api.SpeechMateApi
import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.OnBoardingDto
import com.speech.network.model.auth.SignupKakaoRequest
import com.speech.network.model.auth.SignupKakaoResponse
import javax.inject.Inject

class AuthDatSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi
) : AuthDataSource {
    override suspend fun loginKakao(idToken: String): Result<LoginKakaoResponse> =
        speechMateApi.loginKakao(LoginKakaoRequest(idToken, "KAKAO"))

    override suspend fun signupKakao(idToken: String, skill: String): Result<SignupKakaoResponse> =
        speechMateApi.signupKakao(SignupKakaoRequest(idToken, OnBoardingDto(skill)))
}