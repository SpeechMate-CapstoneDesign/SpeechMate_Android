package com.speech.network.source.auth

import com.speech.network.api.SpeechMateApi
import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.OnBoardingDto
import com.speech.network.model.auth.SignUpKakaoRequest
import com.speech.network.model.auth.SignUpKakaoResponse

import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi
) : AuthDataSource {
    override suspend fun loginKakao(idToken: String): Result<LoginKakaoResponse> =
        speechMateApi.loginKakao(LoginKakaoRequest(idToken, KAKAO_PROVIDER))

    override suspend fun signupKakao(idToken: String, skill: String): Result<SignUpKakaoResponse> =
        speechMateApi.signupKakao(SignUpKakaoRequest(idToken, OnBoardingDto(skill)))

    companion object {
        private const val KAKAO_PROVIDER = "KAKAO"
    }
}