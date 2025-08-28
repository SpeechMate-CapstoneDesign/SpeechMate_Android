package com.speech.data.repository

import com.speech.common.util.suspendRunCatching
import com.speech.datastore.datasource.LocalTokenDataSource
import com.speech.domain.repository.AuthRepository
import com.speech.network.source.auth.AuthDataSource
import com.speech.network.token.TokenManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenManager: TokenManager,
    private val localTokenDataSource: LocalTokenDataSource
) : AuthRepository {
    override suspend fun checkSession() {
        val refreshToken = tokenManager.getRefreshToken()
        val response = authDataSource.checkSession(refreshToken)

        coroutineScope {
            val accessTokenJob = launch {
                response.access.let { tokenManager.setAccessToken(it) }
            }

            val refreshTokenJob = launch {
                response.refresh.let { tokenManager.setRefreshToken(it) }
            }

            joinAll(accessTokenJob, refreshTokenJob)
        }
    }

    override suspend fun loginKakao(idToken: String): Boolean {
        val response = authDataSource.loginKakao(idToken)

        if (!response.newUser) {
            coroutineScope {
                val accessTokenJob = launch {
                    response.access?.let { tokenManager.setAccessToken(it) }
                }

                val refreshTokenJob = launch {
                    response.refresh?.let { tokenManager.setRefreshToken(it) }
                }

                joinAll(accessTokenJob, refreshTokenJob)
            }
        }

        return response.newUser
    }

    override suspend fun signupKakao(idToken: String, skills: List<String>) {
        val response = authDataSource.signupKakao(idToken = idToken, skills = skills)

        coroutineScope {
            val accessTokenJob = launch {
                response.access.let { tokenManager.setAccessToken(it) }
            }

            val refreshTokenJob = launch {
                response.refresh.let { tokenManager.setRefreshToken(it) }
            }

            joinAll(accessTokenJob, refreshTokenJob)
        }
    }

    override suspend fun logout() {
        // localTokenDataSource.setAccessToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiY2F0ZWdvcnkiOiJhY2Nlc3MiLCJpYXQiOjE3NTYzNzk4NDIsImV4cCI6MTc1NjM4MzQ0Mn0.6xZZ3iaAAIrqJBAVR6rz0MkKez0DReYpZHWVZHgkAN0\",\"refresh\":\"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0IiwiY2F0ZWdvcnkiOiJyZWZyZXNoIiwiaWF0IjoxNzU2Mzc5ODQyLCJleHAiOjE3NTY5ODQ2NDJ9.t6mfmibuRhN9LgRpEIMIraf1dSzrx5C-9yRfHLiLf7o")

        coroutineScope {
            val clearTokenJob = launch {
                localTokenDataSource.clearToken()
            }

            clearTokenJob.join()
        }

        authDataSource.logout()
    }

    override suspend fun unRegisterUser() {
        authDataSource.unRegisterUser()

        coroutineScope {
            val clearTokenJob = launch {
                localTokenDataSource.clearToken()
            }

            clearTokenJob.join()
        }
    }

}