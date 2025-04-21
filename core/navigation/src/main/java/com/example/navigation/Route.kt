package com.example.navigation

import kotlinx.serialization.Serializable

sealed interface Route

@kotlinx.serialization.Serializable
data object LoginRoute : Route

@Serializable
data object HomeRoute : Route


