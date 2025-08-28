package com.speech.mypage.graph.setting

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState

data class SettingState(
    val showLogoutDialog: Boolean = false,
    val showUnRegisterDialog: Boolean = false
) : UiState

sealed class SettingIntent : UiIntent {
    data object OnBackPressed : SettingIntent()
    data object OnPolicyClick : SettingIntent()
    data object OnLogout : SettingIntent()
    data object OnUnRegister : SettingIntent()
    data object OnInquiry : SettingIntent()
}

sealed interface SettingSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : SettingSideEffect
    data object NavigateToBack : SettingSideEffect
    data object NavigateToPolicy : SettingSideEffect
    data object NavigateToInquiry : SettingSideEffect
    data object NavigateToLogin : SettingSideEffect
}