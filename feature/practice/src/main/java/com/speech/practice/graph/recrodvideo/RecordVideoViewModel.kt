package com.speech.practice.graph.recrodvideo

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.repository.SpeechRepository
import com.speech.practice.graph.practice.PracticeSideEffect
import com.speech.practice.graph.recordaudio.RecordingAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.use

@HiltViewModel
class RecordVideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
) : ContainerHost<RecordVideoState, RecordVideoSideEffect>, ViewModel() {
    override val container = container<RecordVideoState, RecordVideoSideEffect>(RecordVideoState())

    fun onIntent(event: RecordVideoIntent) {
        when (event) {
            is RecordVideoIntent.OnRequestPermissionFailure -> intent {
                postSideEffect(RecordVideoSideEffect.ShowSnackBar("녹화를 위해 카메라 접근 권한 허용이 필요합니다."))
            }
            is RecordVideoIntent.StartRecording -> {}
            is RecordVideoIntent.FinishRecording -> {}
            is RecordVideoIntent.CancelRecording -> {}
            is RecordVideoIntent.PauseRecording -> {}
            is RecordVideoIntent.ResumeRecording -> {}
            is RecordVideoIntent.OnBackPressed -> intent {
                postSideEffect(RecordVideoSideEffect.NavigateBack)
            }

            is RecordVideoIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is RecordVideoIntent.OnRequestFeedback -> onRequestFeedback()
        }
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingVideoState != RecordingAudioState.Completed) return@intent

//        if (!validateSpeechFile(audioFile.toUri())) {
//            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
//            return@intent
//        }
//
//        suspendRunCatching {
//            speechRepository.uploadFromPath(
//                filePath = audioFile.path,
//                speechConfig = state.speechConfig
//            )
//        }.onSuccess { speechId ->
//            postSideEffect(RecordAudioSideEffect.NavigateToFeedback(speechId))
//        }.onFailure {
//            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
//        }
    }

    fun startRecording() {

    }



}