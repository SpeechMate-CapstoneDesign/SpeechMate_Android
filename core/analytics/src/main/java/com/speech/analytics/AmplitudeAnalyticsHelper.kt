package com.speech.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.amplitude.android.Amplitude
import com.amplitude.android.events.BaseEvent
import com.speech.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.speech.analytics.AnalyticsEvent.Types.SCREEN_VIEW
import javax.inject.Inject


class AmplitudeAnalyticsHelper @Inject constructor(
    private val amplitude: Amplitude,
) : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        amplitude.track(event.toAmplitudeEvent())
    }

    override fun setUserId(id: String) {
        amplitude.setUserId(id)
    }

    override fun clearUserId() {
        amplitude.setUserId(null)
    }

    private fun AnalyticsEvent.toAmplitudeEvent(): BaseEvent = BaseEvent().apply {
        this.eventType = type
        this.eventProperties = properties
    }
}



