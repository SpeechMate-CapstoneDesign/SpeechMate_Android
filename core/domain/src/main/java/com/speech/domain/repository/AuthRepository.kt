package com.speech.domain.repository

interface AuthRepository {
    suspend fun checkSession()
    suspend fun loginKakao(idToken: String): Boolean
    suspend fun signupKakao(idToken: String, skills: List<String>)
    suspend fun logout()
    suspend fun unRegisterUser()
}