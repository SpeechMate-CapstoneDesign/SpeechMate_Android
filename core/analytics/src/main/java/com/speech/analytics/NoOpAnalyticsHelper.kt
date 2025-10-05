package com.speech.analytics

import android.util.Log

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        Log.d("NoOpAnalyticsHelper", event.toString())
    }
    override fun setUserId(id: String) = Unit
    override fun clearUserId() = Unit
}
