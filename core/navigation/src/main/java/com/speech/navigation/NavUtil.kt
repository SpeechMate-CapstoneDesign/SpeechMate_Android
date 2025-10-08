package com.speech.navigation

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
    MyPageGraph::WebViewRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean =
    this?.route?.let { route ->
        HIDDEN_BOTTOM_BAR_ROUTES.any { hiddenRoute ->
            route.startsWith(hiddenRoute.qualifiedName ?: "")
        }
    } ?: false

fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

fun NavDestination?.containsRoute(routes: List<KClass<*>>): Boolean {
    val currentRoute = this?.route ?: return false
    return routes.mapNotNull { it.simpleName }.any { currentRoute.contains(it) }
}

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
    route.startsWith(SplashRoute::class.qualifiedName.orEmpty()) -> "splash"
    else -> null
}
