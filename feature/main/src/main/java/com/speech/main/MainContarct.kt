package com.speech.main

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState

//data class MainState(
//    val isLoading: Boolean = true
//) : UiState

sealed class MainIntent : UiIntent {

}
sealed interface MainSideEffect : UiSideEffect {
    data object NavigateToLogin : MainSideEffect
    data object NavigateToPractice : MainSideEffect
}