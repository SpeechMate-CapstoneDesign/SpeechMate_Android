package com.speech.domain.repository

import kotlinx.coroutines.flow.SharedFlow

interface NotificationRepository {
    val notificationEvents: SharedFlow<NotificationEvent>
    suspend fun updateDeviceToken(token: String)
    suspend fun onNonVerbalAnalysisCompleted(speechId: Int)

    sealed class NotificationEvent {
        data class NonVerbalCompleted(val speechId: Int) : NotificationEvent()
    }
}
