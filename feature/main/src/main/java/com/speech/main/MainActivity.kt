package com.speech.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.designsystem.component.SpeechMateSnackBar
import com.example.designsystem.component.SpeechMateSnackBarHost
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.LocalAnalyticsHelper
import com.speech.auth.navigation.navigateToLogin
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.SpeechMateBottomBarAnimation
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppBottomBar
import com.speech.main.navigation.AppNavHost
import com.speech.navigation.shouldHideBottomBar
import com.speech.practice.navigation.navigateToPractice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        requestPermissions(this)

        setContent {
            val navController = rememberNavController()
            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination
            val snackBarHostState = remember { SnackbarHostState() }

            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState,
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                SpeechMateTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = SmTheme.colors.background,
                        snackbarHost = {
                            SpeechMateSnackBarHost(
                                hostState = snackBarHostState,
                                snackbar = { snackBarData -> SpeechMateSnackBar(snackBarData) },
                            )
                        },
                        bottomBar = {
                            SpeechMateBottomBarAnimation(
                                visible = currentDestination?.shouldHideBottomBar() == false,
                                modifier = Modifier.navigationBarsPadding(),
                            ) {
                                AppBottomBar(
                                    currentDestination = currentDestination,
                                    navigateToBottomNaviDestination = { bottomNaviDestination ->
                                        navController.navigate(
                                            bottomNaviDestination,
                                            navOptions = navOptions {
                                                popUpTo(0) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            },
                                        )
                                    },
                                )
                            }
                        },
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            Modifier.padding(innerPadding),
                        )

                    }
                }

                LaunchedEffect(Unit) {
                    viewModel.container.sideEffectFlow.collect { sideEffect ->
                        when (sideEffect) {
                            is MainSideEffect.NavigateToPractice -> {
                                navController.navigateToPractice(
                                    navOptions {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    },
                                )
                            }

                            is MainSideEffect.NavigateToLogin -> {
                                navController.navigateToLogin(
                                    navOptions {
                                        popUpTo(0) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun requestPermissions(activity: Activity) {
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
    )

    ActivityCompat.requestPermissions(activity, permissions, 1001)
}





