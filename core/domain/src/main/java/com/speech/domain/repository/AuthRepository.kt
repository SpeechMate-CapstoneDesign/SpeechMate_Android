package com.speech.domain.repository

interface AuthRepository {
    suspend fun checkSession()
    suspend fun loginKakao(idToken: String): Pair<Boolean, Int>
    suspend fun signupKakao(idToken: String, skills: List<String>) : Int
    suspend fun logout()
    suspend fun unRegisterUser()
}
