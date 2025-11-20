package com.speech.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface NotificationRepository {
    val notificationEvents: SharedFlow<NotificationEvent>
    suspend fun updateDeviceToken(token: String)
    suspend fun onNonVerbalAnalysisCompleted(speechId: Int, speechName : String)

    sealed class NotificationEvent {
        data class NonVerbalCompleted(val speechId: Int, val speechName : String) : NotificationEvent()
    }
}
