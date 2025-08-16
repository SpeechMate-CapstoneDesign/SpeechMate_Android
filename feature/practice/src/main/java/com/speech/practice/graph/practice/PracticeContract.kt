package com.speech.practice.graph.practice

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect

sealed class PracticeIntent : UiIntent {
    data class OnUploadSpeechFile(val uri : Uri) : PracticeIntent()
    data object OnRecordAudioClick : PracticeIntent()
}

sealed interface PracticeSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : PracticeSideEffect
    data object NavigateToRecordAudio : PracticeSideEffect
}