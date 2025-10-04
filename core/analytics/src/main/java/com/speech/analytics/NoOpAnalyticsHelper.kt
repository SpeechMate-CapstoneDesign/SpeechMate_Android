package com.speech.analytics

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
    override fun setUserId(id: String) = Unit
}
