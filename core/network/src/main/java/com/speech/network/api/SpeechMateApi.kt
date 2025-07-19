package com.speech.network.api

import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.SignupKakaoRequest
import com.speech.network.model.auth.SignupKakaoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SpeechMateApi {
    // Auth
    @POST("/api/auth/oauth/kakao/login")
    suspend fun loginKakao(@Body loginKakaoRequest: LoginKakaoRequest) : Result<LoginKakaoResponse>

    @POST("/api/auth/oauth/kakao/signup")
    suspend fun signupKakao(@Body signUpKakaoRequest: SignupKakaoRequest) : Result<SignupKakaoResponse>
}