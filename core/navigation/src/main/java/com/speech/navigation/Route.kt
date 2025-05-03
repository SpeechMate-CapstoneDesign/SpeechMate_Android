package com.speech.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object LoginRoute : Route

@Serializable
data object OnBoardingRoute : Route

@Serializable
data object PracticeBaseRoute : Route

sealed class PracticeGraph : Route {
    @Serializable
    data object PracticeRoute : Route
}
