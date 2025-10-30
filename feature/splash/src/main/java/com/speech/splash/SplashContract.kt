package com.speech.splash

import com.speech.common.base.UiSideEffect

sealed interface SplashSideEffect : UiSideEffect {
    data object NavigateToLogin : SplashSideEffect
    data object NavigateToPractice : SplashSideEffect
}
