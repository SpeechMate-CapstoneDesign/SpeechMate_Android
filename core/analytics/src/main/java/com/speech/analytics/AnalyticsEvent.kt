package com.speech.analytics

data class AnalyticsEvent(
    val type: String,
    val properties: MutableMap<String, Any?>? = null,
) {
    object Types {
        const val SCREEN_VIEW = "screen_view"
        const val ACTION = "action"
    }

    object PropertiesKeys {
        const val SCREEN_NAME = "screen_name"
        const val ACTION_NAME = "action_name"
    }
}
