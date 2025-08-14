package com.speech.domain.repository

interface AuthRepository {
    suspend fun loginKakao(idToken: String) : Boolean
    // suspend fun signupKakao(idToken: String, skill : String)

//    suspend fun logOut(): Result<Unit>
//
//    suspend fun unregisterUser(): Result<Unit>
}