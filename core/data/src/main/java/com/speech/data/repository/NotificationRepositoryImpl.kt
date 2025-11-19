package com.speech.data.repository

import android.content.Context
import com.speech.domain.repository.NotificationRepository
import com.speech.network.source.notification.NotificationDataSource
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource,
) : NotificationRepository {
    override suspend fun updateDeviceToken(token: String) {
        notificationDataSource.updateDeviceToken(token)
    }
}
