package com.speech.common_ui.ui

import android.content.Context
import androidx.compose.runtime.remember
import com.speech.common_ui.util.findActivity


import android.app.Activity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import javax.inject.Singleton

@Composable
fun rememberSystemUiController(): SystemUiController? {
    val context = LocalContext.current
    return remember {
        context.findActivity()?.let { activity ->
            SystemUiController(activity)
        }
    }
}
class SystemUiController(private val activity: Activity) {

    private val windowInsetsController = WindowCompat.getInsetsController(
        activity.window,
        activity.window.decorView
    )

    fun hideSystemBars() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 화면 계속 켜짐
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun hideStatusBar() {
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    fun showSystemBars() {
        // 상태바, 네비게이션 바 표시
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())

        // 화면 켜짐 플래그 제거
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}
