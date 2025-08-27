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
import com.speech.practice.graph.recrodvideo.RecordVideoRoute
import com.speech.practice.graph.recrodvideo.RecordVideoScreen


fun NavController.navigateToPractice(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.PracticeRoute, navOptions)
}

fun NavController.navigateToRecordAudio(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.RecordAudioRoute, navOptions)
}

fun NavController.navigateToRecordVideo(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.RecordVideoRoute, navOptions)
}

fun NavController.navigateToFeedback(speechId: Int, navOptions: NavOptions? = null) {
    navigate(PracticeGraph.FeedbackRoute(speechId), navOptions)
}


fun NavGraphBuilder.practiceNavGraph(
    navigateBack: () -> Unit,
    navigateToRecordAudio: () -> Unit,
    navigateToRecordVideo: () -> Unit,
    navigateToFeedBack: (Int) -> Unit
) {
    navigation<PracticeBaseRoute>(startDestination = PracticeGraph.PracticeRoute) {
        composable<PracticeGraph.PracticeRoute> {
            PracticeRoute(
                navigateToRecordAudio = navigateToRecordAudio,
                navigateToRecordVideo = navigateToRecordVideo,
                navigateToFeedback = navigateToFeedBack
            )
        }

        composable<PracticeGraph.RecordAudioRoute> {
            RecordAudioRoute(
                navigateBack = navigateBack, navigateToFeedBack = navigateToFeedBack
            )
        }

        composable<PracticeGraph.RecordVideoRoute> {
            RecordVideoRoute(
                navigateBack = navigateBack, navigateToFeedBack = navigateToFeedBack
            )
        }

    }
}