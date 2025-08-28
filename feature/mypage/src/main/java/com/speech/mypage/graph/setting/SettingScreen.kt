package com.speech.mypage.graph.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SettingRoute(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToPolicy: () -> Unit,
    navigateToInquiry: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is SettingSideEffect.NavigateToPolicy -> navigateToPolicy()
            is SettingSideEffect.NavigateToInquiry -> navigateToInquiry()
            is SettingSideEffect.NavigateToLogin -> navigateToLogin()
        }
    }

    SettingScreen(
        onPolicyClick = { viewModel.onIntent(SettingIntent.OnPolicyClick) },
        onLogout = { viewModel.onIntent(SettingIntent.OnLogout) },
        onUnRegister = { viewModel.onIntent(SettingIntent.OnUnRegister) },
        onInquiry = { viewModel.onIntent(SettingIntent.OnInquiry) }
    )
}

@Composable
private fun SettingScreen(
    onPolicyClick: () -> Unit,
    onLogout: () -> Unit,
    onUnRegister: () -> Unit,
    onInquiry: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {

            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingScreenPreview() {
    SettingScreen(
        onPolicyClick = {},
        onLogout = {},
        onUnRegister = {},
        onInquiry = {}
    )
}