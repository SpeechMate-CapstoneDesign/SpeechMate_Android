package com.speech.network.source.auth

import com.speech.network.api.SpeechMateApi
import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.OnBoardingDto
import com.speech.network.model.auth.ReissueTokenRequest
import com.speech.network.model.auth.ReissueTokenResponse
import com.speech.network.model.auth.SignUpKakaoRequest
import com.speech.network.model.auth.SignUpKakaoResponse
import com.speech.network.model.getData

import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi
) : AuthDataSource {
    override suspend fun checkSession(refreshToken: String): ReissueTokenResponse =
        speechMateApi.reissueToken(ReissueTokenRequest(refreshToken)).getData()

    override suspend fun loginKakao(idToken: String): LoginKakaoResponse =
        speechMateApi.loginKakao(LoginKakaoRequest(idToken, KAKAO_PROVIDER)).getData()

    override suspend fun signupKakao(idToken: String, skills: List<String>): SignUpKakaoResponse =
        speechMateApi.signupKakao(SignUpKakaoRequest(idToken, OnBoardingDto(skills))).getData()

    override suspend fun logout() =
        speechMateApi.logout().getData()

    override suspend fun unRegisterUser() =
        speechMateApi.unRegisterUser().getData()

    companion object {
        private const val KAKAO_PROVIDER = "KAKAO"
    }
}