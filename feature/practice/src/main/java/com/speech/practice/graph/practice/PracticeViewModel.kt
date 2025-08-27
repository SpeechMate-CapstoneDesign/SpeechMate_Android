package com.speech.practice.graph.practice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
) : ContainerHost<PractieState, PracticeSideEffect>, ViewModel() {
    override val container = container<PractieState, PracticeSideEffect>(PractieState())

    fun onIntent(event: PracticeIntent) {
        when (event) {
            is PracticeIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is PracticeIntent.OnUploadSpeechFile -> onUploadSpeechFile(event.uri)
            is PracticeIntent.OnRecordAudioClick -> intent {
                postSideEffect(PracticeSideEffect.NavigateToRecordAudio)
            }
            is PracticeIntent.OnRecordVideoClick -> intent {
                postSideEffect(PracticeSideEffect.NavigateToRecordVideo)
            }
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
    }

    fun onUploadSpeechFile(uri: Uri) = intent {
        if (!validateSpeechFile(uri)) {
            postSideEffect(PracticeSideEffect.ShowSnackBar("발표 파일은 1분이상 20분 이하만 업로드 가능합니다."))
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadFromUri(uri.toString(), state.speechConfig)
        }.onSuccess { speechId ->
            postSideEffect(PracticeSideEffect.NavigateToFeedback(speechId))
        }.onFailure {
            postSideEffect(PracticeSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
        }
    }

}


