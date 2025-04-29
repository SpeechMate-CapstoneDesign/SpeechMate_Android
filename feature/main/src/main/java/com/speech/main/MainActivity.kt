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
import androidx.core.app.ActivityCompat
import com.speech.speechmate.graph.home.HomeScreen

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

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
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    HomeScreen()
                }

            }
        }
    }
}

