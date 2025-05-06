package com.speech.main.navigation

import com.speech.practice.navigation.practiceNavGraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.speech.navigation.PracticeBaseRoute
import com.speech.practice.navigation.navigateToRecordAudio


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = PracticeBaseRoute,
        modifier = modifier,
    ) {
        val currentRoute = navController.currentDestination?.route

        practiceNavGraph(
            navigateBack = { navigateBack(navController) },
            navigateToRecordAudio = { navController.navigateToRecordAudio() }
        )
    }
}

private fun navigateBack(
    navController: NavHostController
) {
    navController.popBackStack()
}
