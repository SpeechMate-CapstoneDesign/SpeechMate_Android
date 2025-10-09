package com.speech.navigation

import android.util.Log
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
    SplashRoute::class,
    AuthGraph.LoginRoute::class,
    AuthGraph.OnBoardingRoute::class,
    PracticeGraph.RecordAudioRoute::class,
    PracticeGraph.RecordVideoRoute::class,
    PracticeGraph.FeedbackRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean =
    this?.route?.let { route ->
        val isHiddenRoute = HIDDEN_BOTTOM_BAR_ROUTES.any { hiddenRoute ->
            route.startsWith(hiddenRoute.qualifiedName ?: "")
        }

        val isWebViewRoute = route.startsWith(MyPageGraph.WebViewRoute::class.qualifiedName ?: "")

        isHiddenRoute || isWebViewRoute
    } ?: false

fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

fun NavDestination.getRouteName(): String? = this.route?.let { mapRouteToName(it) }

private fun mapRouteToName(route: String): String? = when {
    route.startsWith(AuthGraph.LoginRoute::class.qualifiedName.orEmpty()) -> "login"
    route.startsWith(AuthGraph.OnBoardingRoute::class.qualifiedName.orEmpty()) -> "onboarding"
    route.startsWith(PracticeGraph.PracticeRoute::class.qualifiedName.orEmpty()) -> "practice"
    route.startsWith(PracticeGraph.RecordAudioRoute::class.qualifiedName.orEmpty()) -> "record_audio"
    route.startsWith(PracticeGraph.RecordVideoRoute::class.qualifiedName.orEmpty()) -> "record_video"
    route.startsWith(PracticeGraph.FeedbackRoute::class.qualifiedName.orEmpty()) -> "feedback"
    route.startsWith(MyPageGraph.MyPageRoute::class.qualifiedName.orEmpty()) -> "my_page"
    route.startsWith(MyPageGraph.SettingRoute::class.qualifiedName.orEmpty()) -> "setting"
    route.startsWith(MyPageGraph.WebViewRoute::class.qualifiedName.orEmpty()) -> "webview"
    route.startsWith(SplashRoute::class.qualifiedName.orEmpty()) -> "splash"
    else -> null
}
