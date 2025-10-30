package com.speech.main.navigation

import com.speech.practice.navigation.practiceNavGraph
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.speech.auth.navigation.authNavGraph
import com.speech.auth.navigation.navigateToLogin
import com.speech.auth.navigation.navigateToOnBoarding
import com.speech.mypage.navigation.myPageNavGraph
import com.speech.mypage.navigation.navigateToSetting
import com.speech.mypage.navigation.navigateToWebView
import com.speech.navigation.SplashRoute
import com.speech.practice.navigation.navigateToFeedback
import com.speech.practice.navigation.navigateToPractice
import com.speech.practice.navigation.navigateToRecordAudio
import com.speech.practice.navigation.navigateToRecordVideo
import com.speech.splash.navigation.splashScreen

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
        splashScreen(
            navigateToPractice = {
                navController.navigateToPractice(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            navigateToLogin = {
                navController.navigateToLogin(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
        )

        authNavGraph(
            navigateToPractice = {
                navController.navigateToPractice(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            navigateToOnBoarding = { idToken ->
                navController.navigateToOnBoarding(
                    idToken,
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
        )

        practiceNavGraph(
            navigateBack = navController::popBackStack,
            navigateToRecordAudio = {
                navController.navigateToRecordAudio(
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
            navigateToRecordVideo = {
                navController.navigateToRecordVideo(
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
            navigateToFeedback = { speechId, fileUrl, speechFileType, speechConfig ->
                navController.navigateToFeedback(
                    speechId = speechId,
                    fileUrl = fileUrl,
                    speechFileType = speechFileType,
                    speechConfig = speechConfig,
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
        )

        myPageNavGraph(
            navigateBack = navController::popBackStack,
            navigateToLogin = {
                navController.navigateToLogin(
                    navOptions {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    },
                )
            },
            navigateToSetting = {
                navController.navigateToSetting(
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
            navigateToFeedBack = { speechId, url, speechFileType, speechConfig ->
                navController.navigateToFeedback(
                    speechId, url, speechFileType, speechConfig,
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
            navigateToWebView = { url ->
                navController.navigateToWebView(
                    url,
                    navOptions {
                        launchSingleTop = true
                    },
                )
            },
        )
    }
}
