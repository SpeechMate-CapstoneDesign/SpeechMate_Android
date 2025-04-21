package com.example.navigation

import androidx.navigation.NavDestination
import kotlin.reflect.KClass

private val HIDDEN_BOTTOM_BAR_ROUTES = setOf(
    LoginRoute::class,
)

fun NavDestination?.shouldHideBottomBar(): Boolean =
    this?.route?.let { route ->
        HIDDEN_BOTTOM_BAR_ROUTES.any { hiddenRoute ->
            route.startsWith(hiddenRoute.qualifiedName ?: "")
        }
    } ?: false


fun NavDestination?.eqaulsRoute(route: KClass<*>): Boolean =
    this?.route?.startsWith(route.qualifiedName ?: "") == true