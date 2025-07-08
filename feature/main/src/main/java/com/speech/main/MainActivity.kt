package com.speech.main

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.designsystem.component.SpeechMateSnackBar
import com.example.designsystem.component.SpeechMateSnackBarHost
import com.kakao.sdk.common.util.Utility
import com.speech.common.event.SpeechMateEvent
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val keyHash = Utility.getKeyHash(this)
        Log.d("kakao keyHash", keyHash)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination
            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(Unit) {
                launch {
                    viewModel.eventHelper.eventChannel.collect { event ->
                        when (event) {
                            is SpeechMateEvent.ShowSnackBar -> snackBarHostState.showSnackbar(event.message)
                        }
                    }
                }
            }

            SpeechMateTheme {
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

