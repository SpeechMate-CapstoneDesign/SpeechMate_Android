package com.speech.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.speech.navigation.SplashRoute

fun NavGraphBuilder.splashScreen(

) {
    composable<SplashRoute> {
        SplashRoute()
    }
}
