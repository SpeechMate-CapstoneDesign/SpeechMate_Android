package com.speech.analytics.di

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.speech.analytics.AmplitudeAnalyticsHelper
import com.speech.analytics.AnalyticsEvent
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.speech.analytics.BuildConfig
import com.speech.analytics.NoOpAnalyticsHelper
import com.speech.analytics.error.FirebaseErrorHelper
import com.speech.analytics.error.NoOpErrorHelper

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun providesAmplitude(@ApplicationContext context: Context): Amplitude = Amplitude(
        Configuration(
            apiKey = BuildConfig.AMPLITUDE_API_KEY,
            context = context,
        ),
    )

    @Provides
    @Singleton
    fun providesFirebaseCrashlytics(@ApplicationContext context: Context): FirebaseCrashlytics =
        FirebaseCrashlytics.getInstance()



    @Provides
    @Singleton
    fun provideAnalyticsHelper(
        @ApplicationContext context: Context,
        amplitude: Amplitude,
    ): AnalyticsHelper {
        return if (BuildConfig.DEBUG) {
            NoOpAnalyticsHelper()
        } else {
            AmplitudeAnalyticsHelper(amplitude)
        }
    }

    @Provides
    @Singleton
    fun provideErrorHelper(
        firebaseCrashlytics: FirebaseCrashlytics,
    ): ErrorHelper {
        return if (BuildConfig.DEBUG) {
            NoOpErrorHelper()
        } else {
            FirebaseErrorHelper(firebaseCrashlytics)
        }
    }
}
