package com.speech.main.navigation

import com.speech.practice.navigation.practiceNavGraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.speech.auth.navigation.authNavGraph
import com.speech.auth.navigation.navigateToOnBoarding
import com.speech.navigation.AuthBaseRoute
import com.speech.navigation.PracticeBaseRoute
import com.speech.navigation.PracticeGraph
import com.speech.practice.navigation.navigateToPlayAudio
import com.speech.practice.navigation.navigateToPractice
import com.speech.practice.navigation.navigateToRecordAudio


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
        val currentRoute = navController.currentDestination?.route

        practiceNavGraph(
            navigateBack = { navigateBack(navController) },
            navigateToRecordAudio = { navController.navigateToRecordAudio() },
            navigateToPlayAudio = { audioFilePath -> navController.navigateToPlayAudio(audioFilePath, navOptions {
                popUpTo<PracticeGraph.PracticeRoute>()
            })}
        )

        authNavGraph(
            navigateBack = { navigateBack(navController) },
            navigateToPractice = {
                navController.navigateToPractice(
                    navOptions {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                )
            },
            navigateToOnBoarding = { idToken ->
                navController.navigateToOnBoarding(idToken)
            }
        )
    }
}

private fun navigateBack(
    navController: NavHostController
) {
    navController.popBackStack()
}
