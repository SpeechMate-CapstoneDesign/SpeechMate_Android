package com.speech.navigation

import kotlinx.serialization.Serializable
import java.io.File

sealed interface Route

@Serializable
data object AuthBaseRoute : Route

sealed class AuthGraph : Route {
    @Serializable
    data object LoginRoute : AuthGraph()

    @Serializable
    data class OnBoardingRoute(val idToken : String) : AuthGraph()
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
    data class FeedbackRoute(val speechId : Int): PracticeGraph()
}

@Serializable
data object MyPageBaseRoute : Route

sealed class MyPageGraph : Route {
    @Serializable
    data object MyPageRoute : MyPageGraph()
}
