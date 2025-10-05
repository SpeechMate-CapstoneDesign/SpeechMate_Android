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
import com.speech.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.speech.analytics.AnalyticsEvent.Types.SCREEN_VIEW

interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
    fun setUserId(id : String)
    fun clearUserId()
}
