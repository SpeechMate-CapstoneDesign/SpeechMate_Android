package com.speech.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.designsystem.component.SpeechMateSnackBar
import com.example.designsystem.component.SpeechMateSnackBarHost
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

        requestPermissions(this)

        setContent {
            val navController = rememberNavController()
            val snackBarHostState = remember { SnackbarHostState() }

            SpeechMateTheme {
                CompositionLocalProvider(LocalSnackbarHostState provides snackBarHostState) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.White,
                        snackbarHost = {
                            SpeechMateSnackBarHost(
                                hostState = snackBarHostState,
                                snackbar = { snackBarData -> SpeechMateSnackBar(snackBarData) }
                            )
                        },
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

private fun requestPermissions(activity: Activity) {
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    ActivityCompat.requestPermissions(activity, permissions, 1001)
}





