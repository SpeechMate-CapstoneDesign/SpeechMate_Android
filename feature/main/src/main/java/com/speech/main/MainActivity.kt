package com.speech.main

import android.Manifest
import android.os.Bundle
import android.util.Log
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
import com.kakao.sdk.common.util.Utility
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keyHash = Utility.getKeyHash(this)
        Log.d("kakao keyHash", keyHash)

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

