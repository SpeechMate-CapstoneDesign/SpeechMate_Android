package com.speech.domain.repository

interface AuthRepository {
    suspend fun loginKakao(idToken: String) : Result<Boolean>

    suspend fun signupKakao(idToken: String, skill : String) : Result<Unit>

//    suspend fun logOut(): Result<Unit>
//
//    suspend fun unregisterUser(): Result<Unit>
}