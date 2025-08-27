package com.speech.main.navigation

import com.speech.practice.navigation.practiceNavGraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.speech.auth.navigation.authNavGraph
import com.speech.auth.navigation.navigateToOnBoarding
import com.speech.navigation.AuthBaseRoute
import com.speech.navigation.PracticeBaseRoute
import com.speech.practice.navigation.navigateToFeedback
import com.speech.practice.navigation.navigateToPractice
import com.speech.practice.navigation.navigateToRecordAudio
import com.speech.practice.navigation.navigateToRecordVideo


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AuthBaseRoute,
        modifier = modifier,
    ) {

        practiceNavGraph(
            navigateBack = navController::popBackStack,
            navigateToRecordAudio = navController::navigateToRecordAudio,
            navigateToRecordVideo = navController::navigateToRecordVideo,
            navigateToFeedBack = navController::navigateToFeedback,
        )

        authNavGraph(
            navigateToPractice = {
                navController.navigateToPractice()
            },
            navigateToOnBoarding = { idToken ->
                navController.navigateToOnBoarding(idToken)
            }
        )
    }
}

