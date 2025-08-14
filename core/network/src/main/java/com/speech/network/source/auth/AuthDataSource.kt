package com.speech.network.source.auth

import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.SignUpKakaoResponse

interface AuthDataSource {
    suspend fun loginKakao(idToken : String) : LoginKakaoResponse
    suspend fun signupKakao(idToken : String, skill : String) : SignUpKakaoResponse
}