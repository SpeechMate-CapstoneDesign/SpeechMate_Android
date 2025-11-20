package com.speech.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.speech.designsystem.R
import com.speech.domain.repository.NotificationRepository
import com.speech.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        scope.launch {
            notificationRepository.updateDeviceToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        val speechId = data["speechId"]?.toInt() ?: -1
        val speechName = data["speechName"] ?: ""
        if (speechId > 0) {
            scope.launch {
                notificationRepository.onNonVerbalAnalysisCompleted(speechId, speechName)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val SPEECHMATE_CHANNEL_ID = "speechmate_notification_channel"
        const val SPEECHMATE_CHANNEL_NAME = "SpeechMate 푸시 알림"
        const val SPEECHMATE_CHANNEL_DESCRIPTION = "SpeechMate에서 보내는 푸시 알림을 관리하는 채널입니다."

        fun createNotificationChannel(context: Context) {
            val channel = android.app.NotificationChannel(
                SPEECHMATE_CHANNEL_ID,
                SPEECHMATE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = SPEECHMATE_CHANNEL_DESCRIPTION
            }

            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
