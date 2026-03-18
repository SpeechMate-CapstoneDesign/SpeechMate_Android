package com.speech.practice.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.navigation.PracticeBaseRoute
import com.speech.navigation.PracticeGraph
import com.speech.practice.graph.feedback.FeedbackRoute
import com.speech.practice.graph.practice.PracticeRoute
import com.speech.practice.graph.recordaudio.RecordAudioRoute
import com.speech.practice.graph.recrodvideo.RecordVideoRoute
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun NavController.navigateToPractice(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.PracticeRoute, navOptions)
}

fun NavController.navigateToRecordAudio(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.RecordAudioRoute, navOptions)
}

fun NavController.navigateToRecordVideo(navOptions: NavOptions? = null) {
    navigate(PracticeGraph.RecordVideoRoute, navOptions)
}

fun NavController.navigateToFeedback(
    speechId: Int,
    tab: FeedbackTab,
    navOptions: NavOptions? = null,
) {
    navigate(
        PracticeGraph.FeedbackRoute(
            speechId = speechId,
            tab = tab,
        ),
        navOptions,
    )
}

fun NavController.navigateToFeedback(
    speechId: Int,
    fileUrl: String = "",
    speechFileType: SpeechFileType,
    speechConfig: SpeechConfig,
    navOptions: NavOptions? = null,
) {
    navigate(
        PracticeGraph.FeedbackRoute(
            speechId = speechId,
            speechFileType = speechFileType,
            fileUrl = fileUrl,
            fileName = speechConfig.fileName,
            speechType = speechConfig.speechType,
            audience = speechConfig.audience,
            venue = speechConfig.venue,
        ),
        navOptions,
    )
}


fun NavGraphBuilder.practiceNavGraph(
    innerPadding: PaddingValues,
    navigateBack: () -> Unit,
    navigateToRecordAudio: () -> Unit,
    navigateToRecordVideo: () -> Unit,
    navigateToFeedback: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
) {
    navigation<PracticeBaseRoute>(startDestination = PracticeGraph.PracticeRoute) {
        composable<PracticeGraph.PracticeRoute> {
            PracticeRoute(
                innerPadding = innerPadding,
                navigateToRecordAudio = navigateToRecordAudio,
                navigateToRecordVideo = navigateToRecordVideo,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.RecordAudioRoute> {
            RecordAudioRoute(
                innerPadding = innerPadding,
                navigateBack = navigateBack,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.RecordVideoRoute> {
            RecordVideoRoute(
                innerPadding = innerPadding,
                navigateBack = navigateBack,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.FeedbackRoute> {
            FeedbackRoute(
                innerPadding = innerPadding,
                navigateToBack = navigateBack,
            )
        }
    }
}
