package com.speech.mypage.graph.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.BackButton
import com.speech.common_ui.util.clickable
import com.speech.designsystem.theme.SpeechMateTheme
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

            is SettingSideEffect.NavigateToBack -> navigateToBack()
            is SettingSideEffect.NavigateToPolicy -> navigateToPolicy()
            is SettingSideEffect.NavigateToInquiry -> navigateToInquiry()
            is SettingSideEffect.NavigateToLogin -> navigateToLogin()
        }
    }

    SettingScreen(
        onBackPressed = { viewModel.onIntent(SettingIntent.OnBackPressed) },
        onPolicyClick = { viewModel.onIntent(SettingIntent.OnPolicyClick) },
        onLogout = { viewModel.onIntent(SettingIntent.OnLogout) },
        onUnRegister = { viewModel.onIntent(SettingIntent.OnUnRegister) },
        onInquiry = { viewModel.onIntent(SettingIntent.OnInquiry) }
    )
}

@Composable
private fun SettingScreen(
    onBackPressed: () -> Unit,
    onLogout: () -> Unit,
    onUnRegister: () -> Unit,
    onPolicyClick: () -> Unit,
    onInquiry: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 55.dp)
        ) {
            item {
                Spacer(Modifier.height(15.dp))

                Text(
                    "이용 안내",
                    style = SpeechMateTheme.typography.bodyMSB
                )

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "앱 버전",
                        style = SpeechMateTheme.typography.bodyMM
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        "1.0.0",
                        color = Color.Gray,
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onInquiry()
                        }) {
                    Text(
                        "문의하기",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onPolicyClick()
                        }) {
                    Text(
                        "개인정보처리방침",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.LightGray
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    "계정",
                    style = SpeechMateTheme.typography.bodyMSB
                )

                Spacer(Modifier.height(17.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }) {
                    Text(
                        "로그아웃",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }) {
                    Text(
                        "회원 탈퇴",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackPressed = onBackPressed)

            Spacer(Modifier.width(10.dp))

            Text("설정", style = SpeechMateTheme.typography.bodyMSB)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SettingScreenPreview() {
    SettingScreen(
        onBackPressed = {},
        onPolicyClick = {},
        onLogout = {},
        onUnRegister = {},
        onInquiry = {}
    )
}