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
}

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}

@Composable
fun TrackNavigationDestination(navController: NavHostController) {
    val analyticsHelper = LocalAnalyticsHelper.current

    LifecycleStartEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val screenName = destination.route ?: "Unknown"
            analyticsHelper.logEvent(
                AnalyticsEvent(
                    type = SCREEN_VIEW,
                    properties = mutableMapOf(SCREEN_NAME to screenName)
                )
            )
        }

        navController.addOnDestinationChangedListener(listener)

        onStopOrDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}
