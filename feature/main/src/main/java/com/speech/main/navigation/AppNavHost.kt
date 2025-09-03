package com.speech.main.navigation

import com.speech.practice.navigation.practiceNavGraph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.speech.auth.navigation.authNavGraph
import com.speech.auth.navigation.navigateToLogin
import com.speech.auth.navigation.navigateToOnBoarding
import com.speech.mypage.navigation.myPageNavGraph
import com.speech.mypage.navigation.navigateToSetting
import com.speech.navigation.SplashRoute
import com.speech.practice.navigation.navigateToFeedback
import com.speech.practice.navigation.navigateToPractice
import com.speech.practice.navigation.navigateToRecordAudio
import com.speech.practice.navigation.navigateToRecordVideo
import com.speech.splash.splashScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier,
    ) {
        splashScreen()

        authNavGraph(
            navigateToPractice = {
                navController.navigateToPractice()
            },
            navigateToOnBoarding = { idToken ->
                navController.navigateToOnBoarding(idToken)
            },
        )

        practiceNavGraph(
            navigateBack = navController::popBackStack,
            navigateToRecordAudio = navController::navigateToRecordAudio,
            navigateToRecordVideo = navController::navigateToRecordVideo,
            navigateToFeedback = { speechId, fileUrl, speechFileType, speechConfig ->
                navController.navigateToFeedback(
                    speechId = speechId,
                    speechFileType = speechFileType,
                    speechConfig = speechConfig,
                )
            },
        )

        myPageNavGraph(
            navigateBack = navController::popBackStack,
            navigateToLogin = {
                navController.navigateToLogin(
                    navOptions {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    },
                )
            },
            navigateToSetting = navController::navigateToSetting,
            navigateToFeedBack = navController::navigateToFeedback,
            navigateToPolicy = {},
            navigateToInquiry = {},
        )

    }
}

