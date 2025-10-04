package com.speech.analytics.di

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.speech.analytics.AmplitudeAnalyticsHelper
import com.speech.analytics.AnalyticsEvent
import com.speech.analytics.AnalyticsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.speech.analytics.BuildConfig
import com.speech.analytics.NoOpAnalyticsHelper

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun providesAmplitude(@ApplicationContext context: Context): Amplitude = Amplitude(
        Configuration(
            apiKey = BuildConfig.AMPLITUDE_API_KEY,
            context = context,
        )
    )

    @Provides
    @Singleton
    fun provideAnalyticsHelper(
        @ApplicationContext context: Context,
        amplitude: Amplitude
    ): AnalyticsHelper {
        return if (BuildConfig.DEBUG) {
            NoOpAnalyticsHelper()
        } else {
            AmplitudeAnalyticsHelper(amplitude)
        }
    }

    @Provides
    @Singleton
    fun provideDebugAnalyticsHelper(): AnalyticsHelper = NoOpAnalyticsHelper()

    @Provides
    @Singleton
    fun provideReleaseAnalyticsHelper(amplitude: Amplitude): AnalyticsHelper =
        AmplitudeAnalyticsHelper(amplitude)
}




