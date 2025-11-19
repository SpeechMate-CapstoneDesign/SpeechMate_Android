package com.speech.data.repository

import com.speech.common.util.suspendRunCatching
import com.speech.datastore.datasource.LocalTokenDataSource
import com.speech.domain.repository.AuthRepository
import com.speech.network.source.auth.AuthDataSource
import com.speech.network.source.notification.NotificationDataSource
import com.speech.network.token.TokenManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val tokenManager: TokenManager,
    private val localTokenDataSource: LocalTokenDataSource,
    private val notificationDataSource: NotificationDataSource,
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

    override suspend fun loginKakao(idToken: String): Pair<Boolean, Int> {
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

        // notificationDataSource.postDeviceToken()

        return Pair(response.newUser, response.userId ?: -1)
    }

    override suspend fun signupKakao(idToken: String, skills: List<String>) : Int {
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

        // notificationDataSource.postDeviceToken()

        return response.userId
    }

    override suspend fun logout() {
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
