package com.speech.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
    AuthGraph.LoginRoute::class,
    AuthGraph.OnBoardingRoute::class
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