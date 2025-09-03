package com.speech.practice.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
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
    fileUrl : String = "",
    date: String = "",
    speechFileType: SpeechFileType,
    speechConfig: SpeechConfig,
    navOptions: NavOptions? = null,
) {
    val formattedDate =
        date.ifEmpty {
            LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
        }

    navigate(
        PracticeGraph.FeedbackRoute(
            speechId = speechId,
            speechFileType = speechFileType,
            fileUrl = fileUrl,
            date = formattedDate,
            fileName = speechConfig.fileName,
            speechType = speechConfig.speechType,
            audience = speechConfig.audience,
            venue = speechConfig.venue,
        ),
        navOptions,
    )
}


fun NavGraphBuilder.practiceNavGraph(
    navigateBack: () -> Unit,
    navigateToRecordAudio: () -> Unit,
    navigateToRecordVideo: () -> Unit,
    navigateToFeedback: (Int, SpeechFileType, SpeechConfig) -> Unit,
) {
    navigation<PracticeBaseRoute>(startDestination = PracticeGraph.PracticeRoute) {
        composable<PracticeGraph.PracticeRoute> {
            PracticeRoute(
                navigateToRecordAudio = navigateToRecordAudio,
                navigateToRecordVideo = navigateToRecordVideo,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.RecordAudioRoute> {
            RecordAudioRoute(
                navigateBack = navigateBack,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.RecordVideoRoute> {
            RecordVideoRoute(
                navigateBack = navigateBack,
                navigateToFeedback = navigateToFeedback,
            )
        }

        composable<PracticeGraph.FeedbackRoute> {
            FeedbackRoute(
                navigateToBack = navigateBack,
            )
        }
    }
}
