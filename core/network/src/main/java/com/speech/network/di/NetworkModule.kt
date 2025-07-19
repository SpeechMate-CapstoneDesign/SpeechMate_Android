package com.speech.network.di


import com.speech.network.source.AuthDataSource
import com.speech.network.source.AuthDataSourceImpl
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
}