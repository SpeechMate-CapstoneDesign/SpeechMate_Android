package com.speech.network.source

import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.SignUpKakaoResponse

interface AuthDataSource {
    suspend fun loginKakao(idToken : String) : Result<LoginKakaoResponse>
    suspend fun signupKakao(idToken : String, skill : String) : Result<SignUpKakaoResponse>
}