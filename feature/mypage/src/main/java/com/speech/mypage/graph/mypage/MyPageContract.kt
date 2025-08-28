package com.speech.mypage.graph.mypage

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig

data class MyPageState(
    val mySpeechs: List<Int> = emptyList()
) : UiState

sealed class MyPageIntent : UiIntent {
    data object OnSettingClick : MyPageIntent()
    data class OnSpeechClick(val speechId: Int) : MyPageIntent()
}

sealed interface MyPageSideEffect : UiSideEffect {
    data object NavigateToSetting : MyPageSideEffect
    data class NavigateToFeedback(val speechId: Int) : MyPageSideEffect
}