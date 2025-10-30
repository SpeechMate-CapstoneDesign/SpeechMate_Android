package com.speech.mypage.graph.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.component.BackButton
import com.speech.designsystem.component.CheckCancelDialog
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.mypage.BuildConfig
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SettingRoute(
    navigateToBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToWebView : (String) -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
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
            is SettingSideEffect.NavigateToPolicy -> navigateToWebView(BuildConfig.SPEECHMATE_PRIVACY_POLICY_URL)
            is SettingSideEffect.NavigateToInquiry -> navigateToWebView(BuildConfig.SPEECHMATE_INQUIRY_URL)
            is SettingSideEffect.NavigateToLogin -> navigateToLogin()
        }
    }


    SettingScreen(
        onBackPressed = { viewModel.onIntent(SettingIntent.OnBackPressed) },
        onPolicyClick = { viewModel.onIntent(SettingIntent.OnPolicyClick) },
        onInquiryClick = { viewModel.onIntent(SettingIntent.OnInquiry) },
        onLogout = { viewModel.onIntent(SettingIntent.OnLogout) },
        onUnRegisterUser = { viewModel.onIntent(SettingIntent.OnUnRegisterUser) },
    )
}

@Composable
private fun SettingScreen(
    onBackPressed: () -> Unit,
    onLogout: () -> Unit,
    onUnRegisterUser: () -> Unit,
    onPolicyClick: () -> Unit,
    onInquiryClick: () -> Unit,
) {
    var showLogoutDg by remember { mutableStateOf(false) }
    var showUnRegisterDg by remember { mutableStateOf(false) }

    if (showLogoutDg) {
        CheckCancelDialog(
            title = stringResource(R.string.sign_out),
            content = "정말로 로그아웃 하시겠습니까?",
            onCheck = onLogout,
            onDismiss = { showLogoutDg = false },
        )
    }

    if (showUnRegisterDg) {
        CheckCancelDialog(
            title = stringResource(R.string.unregister_user),
            content = "회원탈퇴 시 모든 정보가 삭제되며, 복구할 수 없습니다. 정말로 탈퇴하시겠습니까?",
            onCheck = onUnRegisterUser,
            onDismiss = { showUnRegisterDg = false },
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 55.dp),
        ) {
            item {
                Spacer(Modifier.height(15.dp))

                SMCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.app_version),
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.weight(1f))

                            Text(
                                text = stringResource(R.string.app_version_value),
                                color = SmTheme.colors.textSecondary,
                                style = SmTheme.typography.bodySM,
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = SmTheme.colors.border,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onInquiryClick()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.inquiry),
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = SmTheme.colors.gray,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = SmTheme.colors.border,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                        onPolicyClick()
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.privacy_policy),
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = SmTheme.colors.gray,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                    }
                }

                Spacer(Modifier.height(20.dp))

                SMCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = rememberDebouncedOnClick {
                                        showLogoutDg = true
                                    },
                                ),
                        ) {
                            Text(
                                text = stringResource(R.string.sign_out),
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = SmTheme.colors.gray,
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = SmTheme.colors.border,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = rememberDebouncedOnClick {
                                        showUnRegisterDg = true
                                    },
                                ),
                        ) {
                            Text(
                                text = stringResource(R.string.unregister_user),
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.weight(1f))

                            Icon(
                                painter = painterResource(R.drawable.ic_chevron_right),
                                contentDescription = null,
                                tint = SmTheme.colors.gray,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BackButton(onBackPressed = onBackPressed)

            Spacer(Modifier.width(10.dp))

            Text(
                text = "설정",
                style = SmTheme.typography.bodyMSB,
                color = SmTheme.colors.textPrimary,
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun SettingScreenPreview() {
    SpeechMateTheme {
        SettingScreen(
            onBackPressed = {},
            onPolicyClick = {},
            onInquiryClick = {},
            onLogout = {},
            onUnRegisterUser = {},
        )
    }
}
