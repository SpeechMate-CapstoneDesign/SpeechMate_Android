package com.speech.data.repository

import com.speech.common.util.suspendRunCatching
import com.speech.datastore.datasource.LocalTokenDataSource
import com.speech.domain.repository.AuthRepository
import com.speech.network.source.auth.AuthDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val localTokenDataSource: LocalTokenDataSource
) : AuthRepository {
    override suspend fun loginKakao(idToken: String): Boolean {
        val response = authDataSource.loginKakao(idToken)

        if (!response.newUser) {

        }
        coroutineScope {
            val accessTokenJob = launch {
                response.access?.let { localTokenDataSource.setAccessToken(it) }
            }

            val refreshTokenJob = launch {
                response.refresh?.let { localTokenDataSource.setRefreshToken(it) }
            }

            joinAll(accessTokenJob, refreshTokenJob)
        }

        return response.newUser
    }

    override suspend fun signupKakao(idToken: String, skills: List<String>) {
        val response = authDataSource.signupKakao(idToken = idToken, skills = skills)

        coroutineScope {
            val accessTokenJob = launch {
                response.access.let { localTokenDataSource.setAccessToken(it) }
            }

            val refreshTokenJob = launch {
                response.refresh.let { localTokenDataSource.setRefreshToken(it) }
            }

            joinAll(accessTokenJob, refreshTokenJob)
        }
    }

    override suspend fun logOut() {
        authDataSource.logout()

        coroutineScope {
            val clearTokenJob = launch {
                localTokenDataSource.clearToken()
            }

            clearTokenJob.join()
        }
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