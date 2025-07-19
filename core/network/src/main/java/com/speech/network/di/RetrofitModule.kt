package com.speech.network.di

import com.speech.network.BuildConfig
import com.speech.network.adapter.SpeechMateCallAdapterFactory
import com.speech.network.api.SpeechMateApi
import com.speech.network.authenticator.SpeechMateAuthenticator
import com.speech.network.interceptor.SpeechMateInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptor: SpeechMateInterceptor,
        authenticator: SpeechMateAuthenticator,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideSpeechMateApi(
        json: Json,
        okHttpClient: OkHttpClient,
        callAdapterFactory : SpeechMateCallAdapterFactory
    ): SpeechMateApi = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .baseUrl(BuildConfig.SPEECHMATE_BASE_URL)
        .build()
        .create(SpeechMateApi::class.java)
}