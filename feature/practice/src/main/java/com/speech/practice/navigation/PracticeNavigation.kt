package com.speech.practice.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.speech.navigation.PracticeBaseRoute
import com.speech.navigation.PracticeGraph
import com.speech.practice.graph.playaudio.PlayAudioRoute
import com.speech.practice.graph.practice.PracticeRoute
import com.speech.practice.graph.recordaudio.RecordAudioRoute


fun NavController.navigateToPractice(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.PracticeRoute, navOptions)
}

fun NavController.navigateToRecordAudio(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.RecordAudioRoute, navOptions)
}

fun NavController.navigateToPlayAudio(audioFilePath: String, navOptions: NavOptions? = null) {
    navigate(PracticeGraph.PlayAudioRoute(audioFilePath), navOptions)
}


fun NavGraphBuilder.practiceNavGraph(
    navigateBack: () -> Unit,
    navigateToRecordAudio: () -> Unit,
    navigateToFeedBack: (Int) -> Unit
) {
    navigation<PracticeBaseRoute>(startDestination = PracticeGraph.PracticeRoute) {
        composable<PracticeGraph.PracticeRoute> {
            PracticeRoute(
                navigateToRecordAudio = navigateToRecordAudio
            )
        }

        composable<PracticeGraph.RecordAudioRoute> {
            RecordAudioRoute(
                navigateBack = navigateBack,
                navigateToFeedBack = navigateToFeedBack
            )
        }

        composable<PracticeGraph.PlayAudioRoute> {
            PlayAudioRoute(
                navigateBack = navigateBack
            )
        }
    }
}