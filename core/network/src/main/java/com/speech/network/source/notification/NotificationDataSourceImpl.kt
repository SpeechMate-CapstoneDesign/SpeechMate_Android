package com.speech.network.source.notification

import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.speech.network.api.SpeechMateApi
import com.speech.network.model.notification.PostDeviceTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi,
    private val firebaseMessaging: FirebaseMessaging,
) : NotificationDataSource {
    override suspend fun updateDeviceToken(token: String) {
        speechMateApi.postDeviceToken(postDeviceTokenRequest = PostDeviceTokenRequest(token))
    }

    override suspend fun postDeviceToken() {
        val token = getDeviceToken()
        return speechMateApi.postDeviceToken(postDeviceTokenRequest = PostDeviceTokenRequest(token))
    }

    override suspend fun getDeviceToken(): String = withContext(Dispatchers.IO) {
        try {
            Tasks.await(firebaseMessaging.token)
        } catch (e: Exception) {
            throw Exception("Failed to get FCM token", e)
        }
    }
}
