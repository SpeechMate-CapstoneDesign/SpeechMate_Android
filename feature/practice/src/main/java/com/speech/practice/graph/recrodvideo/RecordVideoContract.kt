package com.speech.practice.graph.recrodvideo

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.SpeechConfig
import com.speech.practice.graph.practice.PracticeIntent
import java.io.File
import androidx.camera.core.CameraSelector
import com.speech.domain.model.speech.SpeechFileType

data class RecordVideoState(
    val videoFile: File? = null,
    val recordingVideoState: RecordingVideoState = RecordingVideoState.Ready,
    val timeText: String = "00:00",
    val speechConfig: SpeechConfig = SpeechConfig(),
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val isUploadingFile: Boolean = false,
) : UiState

sealed class RecordingVideoState {
    data object Ready : RecordingVideoState()
    data object Recording : RecordingVideoState()
    data object Paused : RecordingVideoState()
    data object Completed : RecordingVideoState()
}

sealed class RecordVideoIntent : UiIntent {
    data object StartRecording : RecordVideoIntent()
    data object PauseRecording : RecordVideoIntent()
    data object ResumeRecording : RecordVideoIntent()
    data object FinishRecording : RecordVideoIntent()
    data object CancelRecording : RecordVideoIntent()
    data object OnBackPressed : RecordVideoIntent()
    data object OnRequestFeedback : RecordVideoIntent()
    data object SwitchCamera : RecordVideoIntent()
    data class OnSpeechConfigChange(val speechConfig: SpeechConfig) : RecordVideoIntent()
}

sealed interface RecordVideoSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : RecordVideoSideEffect
    data object NavigateBack : RecordVideoSideEffect
    data class NavigateToFeedback(
        val speechId: Int,
        val fileUrl: String,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : RecordVideoSideEffect
}
