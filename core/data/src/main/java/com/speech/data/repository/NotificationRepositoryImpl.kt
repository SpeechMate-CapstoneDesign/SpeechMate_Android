package com.speech.data.repository

import android.content.Context
import android.media.audiofx.NoiseSuppressor
import com.speech.domain.repository.NotificationRepository
import com.speech.domain.repository.NotificationRepository.NotificationEvent
import com.speech.domain.repository.SpeechRepository.SpeechUpdateEvent
import com.speech.network.source.notification.NotificationDataSource
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource,
) : NotificationRepository {
    private val _notificationEvents = MutableSharedFlow<NotificationEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()


    override suspend fun updateDeviceToken(token: String) {
        notificationDataSource.updateDeviceToken(token)
    }

    override suspend fun onNonVerbalAnalysisCompleted(speechId: Int, speechName : String) {
       _notificationEvents.emit(NotificationEvent.NonVerbalCompleted(speechId, speechName))
    }
}
