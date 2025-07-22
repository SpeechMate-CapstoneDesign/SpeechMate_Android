package com.speech.data.di

import com.speech.data.repository.AuthRepositoryImpl
import com.speech.data.repository.SpeechRepositoryImpl
import com.speech.data.repository.TokenManagerImpl
import com.speech.domain.repository.AuthRepository
import com.speech.domain.repository.SpeechRepository
import com.speech.network.token.TokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindsAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindsSpeechRepository(
        speechRepositoryImpl: SpeechRepositoryImpl
    ): SpeechRepository

    @Binds
    @Singleton
    abstract fun bindsTokenManager(
        tokenManagerImpl: TokenManagerImpl,
    ): TokenManager
}