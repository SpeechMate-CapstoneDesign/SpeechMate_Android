package com.speech.domain.repository

interface NotificationRepository {
    suspend fun updateDeviceToken(token: String)
}
