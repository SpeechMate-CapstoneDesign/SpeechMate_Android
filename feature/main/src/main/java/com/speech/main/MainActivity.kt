package com.speech.main

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            permissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    SpeechMateTheme {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = Color.White
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
}

