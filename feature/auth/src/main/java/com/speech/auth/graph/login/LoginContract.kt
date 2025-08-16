package com.speech.auth.graph.login

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect


sealed class LoginIntent : UiIntent {
    data class OnLoginClick(val idToken: String) : LoginIntent()
}

sealed interface LoginSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : LoginSideEffect
    data class NavigateToOnBoarding(val idToken: String) : LoginSideEffect
    data object NavigateToPractice : LoginSideEffect
}