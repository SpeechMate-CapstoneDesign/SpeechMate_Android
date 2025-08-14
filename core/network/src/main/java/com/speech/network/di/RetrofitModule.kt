package com.speech.network.di

import com.speech.network.BuildConfig
import com.speech.network.adapter.SpeechMateCallAdapterFactory
import com.speech.network.api.S3Api
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class S3OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @AuthOkHttpClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        interceptor: SpeechMateInterceptor,
        authenticator: SpeechMateAuthenticator,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @S3OkHttpClient
    @Singleton
    @Provides
    fun provideS3OkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideSpeechMateApi(
        json: Json,
        @AuthOkHttpClient okHttpClient: OkHttpClient,
        callAdapterFactory: SpeechMateCallAdapterFactory
    ): SpeechMateApi = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .baseUrl(BuildConfig.SPEECHMATE_BASE_URL)
        .build()
        .create(SpeechMateApi::class.java)

    @Singleton
    @Provides
    fun provideS3Api(
        json: Json,
        @S3OkHttpClient okHttpClient: OkHttpClient,
        callAdapterFactory: SpeechMateCallAdapterFactory
    ): S3Api = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .baseUrl(BuildConfig.SPEECHMATE_BASE_URL)
        .build()
        .create(S3Api::class.java)
}