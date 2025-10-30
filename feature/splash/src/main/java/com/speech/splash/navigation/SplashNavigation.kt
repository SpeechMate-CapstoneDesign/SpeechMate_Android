package com.speech.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.speech.navigation.SplashRoute
import com.speech.splash.SplashRoute

fun NavGraphBuilder.splashScreen(
    navigateToLogin: () -> Unit,
    navigateToPractice: () -> Unit,
) {
    composable<SplashRoute> {
        SplashRoute(
            navigateToLogin = navigateToLogin,
            navigateToPractice = navigateToPractice,
        )
    }
}
