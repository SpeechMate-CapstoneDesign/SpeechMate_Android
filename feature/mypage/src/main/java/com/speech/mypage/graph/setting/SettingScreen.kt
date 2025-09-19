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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.component.BackButton
import com.speech.designsystem.component.CheckCancelDialog
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.theme.SpeechMateTheme
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SettingRoute(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToPolicy: () -> Unit,
    navigateToInquiry: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
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
        onUnRegisterUser = { viewModel.onIntent(SettingIntent.OnUnRegisterUser) },
        onInquiry = { viewModel.onIntent(SettingIntent.OnInquiry) }
    )
}

@Composable
private fun SettingScreen(
    onBackPressed: () -> Unit,
    onLogout: () -> Unit,
    onUnRegisterUser: () -> Unit,
    onPolicyClick: () -> Unit,
    onInquiry: () -> Unit,
) {
    var showLogoutDg by remember { mutableStateOf(false) }
    var showUnRegisterDg by remember { mutableStateOf(false) }

    if (showLogoutDg) {
        CheckCancelDialog(
            title = "로그아웃",
            content = "정말로 로그아웃 하시겠습니까?",
            onCheck = onLogout,
            onDismiss = { showLogoutDg = false }
        )
    }

    if (showUnRegisterDg) {
        CheckCancelDialog(
            title = "회원탈퇴",
            content = "회원탈퇴 시 모든 정보가 삭제되며, 복구할 수 없습니다. 정말로 탈퇴하시겠습니까?",
            onCheck = onUnRegisterUser,
            onDismiss = { showUnRegisterDg = false }
        )
    }


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
                        .clickable(onClick = rememberDebouncedOnClick {
                            onInquiry()
                        })
                ) {
                    Text(
                        "문의하기",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = rememberDebouncedOnClick {
                            onPolicyClick()
                        })
                ) {
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
                        .clickable(onClick = rememberDebouncedOnClick {
                            showLogoutDg = true
                        })
                ) {
                    Text(
                        "로그아웃",
                        style = SpeechMateTheme.typography.bodyMM
                    )
                }

                Spacer(Modifier.height(17.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = rememberDebouncedOnClick {
                            showUnRegisterDg = true
                        })
                ) {
                    Text(
                        "회원탈퇴",
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
        onUnRegisterUser = {},
        onInquiry = {}
    )
}
