package com.speech.practice.graph.recordaudio

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig
import java.util.Locale

data class RecordAudioState(
    val recordingState: RecordingState = RecordingState.Ready,
    val timeText: String = "00 : 00 . 00",
) : UiState

sealed class RecordingState {
    data object Ready : RecordingState()
    data object Recording : RecordingState()
    data object Paused : RecordingState()
    data object Completed : RecordingState()
}

sealed class RecordAudioIntent : UiIntent {
    data object StartRecording : RecordAudioIntent()
    data object PauseRecording : RecordAudioIntent()
    data object ResumeRecording : RecordAudioIntent()
    data object FinishRecording : RecordAudioIntent()
    data object CancelRecording : RecordAudioIntent()
    data object OnBackPressed : RecordAudioIntent()
    data object OnRequestFeedback : RecordAudioIntent()
}

sealed interface RecordAudioSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : RecordAudioSideEffect
    data object NavigateBack : RecordAudioSideEffect
    data class NavigateToFeedback(val speechId: Int) : RecordAudioSideEffect
}
