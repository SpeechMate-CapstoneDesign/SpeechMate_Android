package com.speech.network.source.notification

interface NotificationDataSource {
    suspend fun updateDeviceToken(token: String)
    suspend fun postDeviceToken()
    suspend fun getDeviceToken(): String
}
