package com.speech.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.speech.analytics.AnalyticsEvent.PropertiesKeys.ACTION_NAME
import com.speech.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.speech.analytics.AnalyticsEvent.Types.ACTION
import com.speech.analytics.AnalyticsEvent.Types.SCREEN_VIEW
import kotlin.Any
import kotlin.String

abstract class AnalyticsHelper {
    abstract fun logEvent(event: AnalyticsEvent)
    abstract fun setUserId(id: String)
    abstract fun clearUserId()

    fun trackActionEvent(
        screenName: String,
        actionName: String,
        properties: MutableMap<String, Any?>? = null,
    ) {
        val eventProperties = mutableMapOf<String, Any?>(
            SCREEN_NAME to screenName,
            ACTION_NAME to actionName,
        )

        properties?.let { eventProperties.putAll(it) }

        logEvent(
            AnalyticsEvent(
                type = ACTION,
                properties = eventProperties,
            ),
        )
    }
}
