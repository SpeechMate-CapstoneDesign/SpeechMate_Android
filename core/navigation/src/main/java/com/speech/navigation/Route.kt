package com.speech.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object AuthBaseRoute : Route

sealed class AuthGraph : Route {
    @Serializable
    data object LoginRoute : AuthGraph()
}

@Serializable
data object OnBoardingRoute : Route

@Serializable
data object PracticeBaseRoute : Route

sealed class PracticeGraph : Route {
    @Serializable
    data object PracticeRoute : PracticeGraph()

    @Serializable
    data object RecordAudioRoute : PracticeGraph()
}
