package com.speech.network.di


import com.speech.network.source.auth.AuthDataSource
import com.speech.network.source.auth.AuthDataSourceImpl
import com.speech.network.source.speech.SpeechDataSource
import com.speech.network.source.speech.SpeechDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindsAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource

    @Binds
    @Singleton
    abstract fun bindsSpeechDataSource(speechDataSourceImpl: SpeechDataSourceImpl): SpeechDataSource
}