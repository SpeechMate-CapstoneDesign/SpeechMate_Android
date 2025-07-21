package com.speech.data.repository

import com.speech.common.util.suspendRunCatching
import com.speech.datastore.datasource.LocalTokenDataSource
import com.speech.domain.repository.AuthRepository
import com.speech.network.source.AuthDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val localTokenDataSource: LocalTokenDataSource
) : AuthRepository {
    override suspend fun loginKakao(idToken: String): Result<Boolean> = suspendRunCatching {
        val response = authDataSource.loginKakao(idToken).getOrThrow()

        if (response.data.newUser) {
            response.data.newUser
        } else {
            coroutineScope {
                val accessTokenJob = launch {
                    response.data.access?.let { localTokenDataSource.setAccessToken(it) }
                }

                val refreshTokenJob = launch {
                    response.data.refresh?.let { localTokenDataSource.setRefreshToken(it) }
                }

                joinAll(accessTokenJob, refreshTokenJob)
            }

            response.data.newUser
        }

    }

    override suspend fun signupKakao(idToken: String, skill: String): Result<Unit> {
        TODO("Not yet implemented")
    }

}