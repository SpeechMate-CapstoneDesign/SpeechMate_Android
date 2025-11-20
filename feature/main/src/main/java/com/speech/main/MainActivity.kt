package com.speech.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.designsystem.component.SpeechMateSnackBar
import com.example.designsystem.component.SpeechMateSnackBarHost
import com.kakao.sdk.common.util.Utility
import com.speech.analytics.AnalyticsEvent
import com.speech.analytics.AnalyticsEvent.PropertiesKeys.SCREEN_NAME
import com.speech.analytics.AnalyticsEvent.Types.SCREEN_VIEW
import com.speech.analytics.AnalyticsHelper
import com.speech.auth.navigation.navigateToLogin
import com.speech.common_ui.compositionlocal.LocalSetShouldApplyScaffoldPadding
import com.speech.common_ui.compositionlocal.LocalShouldApplyScaffoldPadding
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.SpeechMateBottomBarAnimation
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.main.navigation.AppBottomBar
import com.speech.main.navigation.AppNavHost
import com.speech.navigation.AuthGraph
import com.speech.navigation.MyPageGraph
import com.speech.navigation.PracticeGraph
import com.speech.navigation.SplashRoute
import com.speech.navigation.getRouteName
import com.speech.navigation.shouldHideBottomBar
import com.speech.practice.navigation.navigateToFeedback
import com.speech.practice.navigation.navigateToPractice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectSideEffect
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
        window.isNavigationBarContrastEnforced = false

        if(intent.extras != null) handleNotificationIntent(intent)

        setContent {
            val navController = rememberNavController()
            val currentDestination = navController.currentBackStackEntryAsState()
                .value?.destination
            val snackbarHostState = remember { SnackbarHostState() }

            viewModel.collectSideEffect { sideEffect ->
                when(sideEffect) {
                    is MainSideEffect.ShowSnackbar -> {
                       val isNoSnackbarRoute = noSnackbarRoutes.any { route ->
                           currentDestination?.hasRoute(route) == true
                       }

                        if(!isNoSnackbarRoute) {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(sideEffect.message)
                        }
                    }
                    is MainSideEffect.NavigateToFeedback -> {
                        navController.navigateToPractice()
                        navController.navigateToFeedback(speechId = sideEffect.speechId, tab = sideEffect.tab)
                    }
                }
            }

            var shouldApplyScaffoldPadding by remember { mutableStateOf(true) }
            val shouldRemovePadding = nonePaddingRoutes.any { route ->
                currentDestination?.hasRoute(route) == true
            }

            CompositionLocalProvider(
                LocalSnackbarHostState provides snackbarHostState,
                LocalShouldApplyScaffoldPadding provides shouldApplyScaffoldPadding,
                LocalSetShouldApplyScaffoldPadding provides { shouldApply ->
                    shouldApplyScaffoldPadding = shouldApply
                },
            ) {
                SpeechMateTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = SmTheme.colors.background,
                        snackbarHost = {
                            SpeechMateSnackBarHost(
                                hostState = snackbarHostState,
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
                            modifier = if (shouldRemovePadding || !shouldApplyScaffoldPadding) {
                                Modifier
                            } else {
                                Modifier.padding(innerPadding)
                            },
                        )
                    }
                }

                // Navigation 감지
                LifecycleStartEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        val screenName = destination.getRouteName()
                        if (screenName != null) {
                            analyticsHelper.logEvent(
                                AnalyticsEvent(
                                    type = SCREEN_VIEW,
                                    properties = mutableMapOf(SCREEN_NAME to screenName),
                                ),
                            )
                        }
                    }

                    navController.addOnDestinationChangedListener(listener)

                    onStopOrDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        intent?.getStringExtra("type")?.let { type ->
            val speechId = intent.getStringExtra("speechId")?.toInt() ?: -1
            if (speechId > 0) {
                viewModel.onIntent(MainIntent.OnNotificationClick(speechId, type))
            }
        }
    }

    companion object {
        private val nonePaddingRoutes = setOf(
            SplashRoute::class,
            PracticeGraph.RecordVideoRoute::class,
        )

        private val noSnackbarRoutes = setOf(
            SplashRoute::class,
            AuthGraph.LoginRoute::class,
            AuthGraph.OnBoardingRoute::class,
            PracticeGraph.RecordVideoRoute::class,
            PracticeGraph.RecordAudioRoute::class,
            PracticeGraph.FeedbackRoute::class,
            MyPageGraph.WebViewRoute::class
        )
    }
}



private fun requestPermissions(activity: Activity) {
    val permissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), 1001)
}





