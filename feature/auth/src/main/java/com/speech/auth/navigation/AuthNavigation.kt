package com.speech.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.speech.auth.graph.login.LoginRoute
import com.speech.auth.graph.onboarding.OnBoardingRoute
import com.speech.navigation.AuthBaseRoute
import com.speech.navigation.AuthGraph

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(AuthGraph.LoginRoute, navOptions)
}

fun NavController.navigateToOnBoarding(idToken: String, navOptions: NavOptions? = null) {
    navigate(AuthGraph.OnBoardingRoute(idToken), navOptions)
}

fun NavGraphBuilder.authNavGraph(
    navigateToPractice: () -> Unit,
    navigateToOnBoarding: (String) -> Unit
) {
    navigation<AuthBaseRoute>(startDestination = AuthGraph.LoginRoute) {
        composable<AuthGraph.LoginRoute> {
            LoginRoute(
                navigateToPractice = navigateToPractice,
                navigateToOnBoarding = navigateToOnBoarding
            )
        }

        composable<AuthGraph.OnBoardingRoute> {
            OnBoardingRoute(
                navigateToPractice = navigateToPractice
            )
        }
    }
}