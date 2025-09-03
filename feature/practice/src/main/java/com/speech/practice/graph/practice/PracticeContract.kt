package com.speech.practice.graph.practice

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType

data class PracticeState(
    val speechConfig: SpeechConfig = SpeechConfig(),
    val isUploadingFile: Boolean = false
) : UiState

sealed class PracticeIntent : UiIntent {
    data class OnUploadSpeechFile(val uri: Uri) : PracticeIntent()
    data class OnSpeechConfigChange(val speechConfig: SpeechConfig) : PracticeIntent()
    data object OnRecordAudioClick : PracticeIntent()
    data object OnRecordVideoClick : PracticeIntent()
}

sealed interface PracticeSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : PracticeSideEffect
    data object NavigateToRecordAudio : PracticeSideEffect
    data object NavigateToRecordVideo : PracticeSideEffect
    data class NavigateToFeedback(val speechId : Int, val speechFileType: SpeechFileType) : PracticeSideEffect
}
