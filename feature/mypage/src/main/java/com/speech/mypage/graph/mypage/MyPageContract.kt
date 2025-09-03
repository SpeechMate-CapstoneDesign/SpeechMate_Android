package com.speech.mypage.graph.mypage

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType

data class MyPageState(
    val mySpeechs: List<Int> = emptyList(),
) : UiState

sealed class MyPageIntent : UiIntent {
    data object OnSettingClick : MyPageIntent()
    data class OnSpeechClick(val speechId: Int, val speechFileType: SpeechFileType, val speechConfig: SpeechConfig) : MyPageIntent()
}

sealed interface MyPageSideEffect : UiSideEffect {
    data object NavigateToSetting : MyPageSideEffect
    data class NavigateToFeedback(
        val speechId: Int,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : MyPageSideEffect
}
