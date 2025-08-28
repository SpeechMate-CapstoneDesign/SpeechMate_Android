package com.speech.network.source.auth

import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.ReissueTokenResponse
import com.speech.network.model.auth.SignUpKakaoResponse

interface AuthDataSource {
    suspend fun checkSession(refreshToken : String) : ReissueTokenResponse
    suspend fun loginKakao(idToken : String) : LoginKakaoResponse
    suspend fun signupKakao(idToken : String, skills : List<String>) : SignUpKakaoResponse
    suspend fun logout()
    suspend fun unRegisterUser()
}