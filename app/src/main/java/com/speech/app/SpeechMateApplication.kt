package com.speech.app

import android.app.Application
import android.util.Log
import com.speech.speechmate.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.speech.app.notification.NotificationService.Companion.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpeechMateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        createNotificationChannel(this)
        initKakao()
    }

    //    private fun initFcm() {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("SpeechMateApplication", "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//
//            val token = task.result
//            Log.d("FCM_TOKEN", token)
//        }
//    }
    private fun initKakao() = KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
}

