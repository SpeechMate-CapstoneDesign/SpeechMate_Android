package com.speech.navigation

import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import kotlinx.serialization.Serializable
import java.io.File
import java.time.LocalDateTime

sealed interface Route

@Serializable
data object SplashRoute : Route

@Serializable
data object AuthBaseRoute : Route

sealed class AuthGraph : Route {
    @Serializable
    data object LoginRoute : AuthGraph()

    @Serializable
    data class OnBoardingRoute(val idToken: String) : AuthGraph()
}


@Serializable
data object PracticeBaseRoute : Route

sealed class PracticeGraph : Route {
    @Serializable
    data object PracticeRoute : PracticeGraph()

    @Serializable
    data object RecordAudioRoute : PracticeGraph()

    @Serializable
    data object RecordVideoRoute : PracticeGraph()

    @Serializable
    data class FeedbackRoute(
        val speechId: Int,
        val speechFileType: SpeechFileType,
        val fileUrl: String,
        // SpeechConfig
        val fileName: String = "",
        val speechType: SpeechType? = null,
        val audience: Audience? = null,
        val venue: Venue? = null,
    ) : PracticeGraph()
}

@Serializable
data object MyPageBaseRoute : Route

sealed class MyPageGraph : Route {
    @Serializable
    data object MyPageRoute : MyPageGraph()

    @Serializable
    data object SettingRoute : MyPageGraph()
}
