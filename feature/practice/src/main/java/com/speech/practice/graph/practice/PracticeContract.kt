package com.speech.practice.graph.practice

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig

data class PractieState(
    val speechConfig: SpeechConfig = SpeechConfig()
) : UiState

sealed class PracticeIntent : UiIntent {
    data class OnUploadSpeechFile(val uri: Uri) : PracticeIntent()
    data class OnSpeechConfigChange(val speechConfig: SpeechConfig) : PracticeIntent()
    data object OnRecordAudioClick : PracticeIntent()
}

sealed interface PracticeSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : PracticeSideEffect
    data object NavigateToRecordAudio : PracticeSideEffect
}