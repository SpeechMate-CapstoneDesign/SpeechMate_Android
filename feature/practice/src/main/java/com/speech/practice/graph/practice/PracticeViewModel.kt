package com.speech.practice.graph.practice

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
) : ContainerHost<Unit, PracticeSideEffect>, ViewModel() {
    override val container = container<Unit, PracticeSideEffect>(Unit)

    fun onIntent(event: PracticeIntent) {
        when (event) {
            is PracticeIntent.OnUploadSpeechFile -> onUploadSpeechFile(event.uri)
            is PracticeIntent.OnRecordAudioClick -> intent {
                postSideEffect(PracticeSideEffect.NavigateToRecordAudio)
            }
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean {
        val durationMs = MediaMetadataRetriever().use { retriever ->
            retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
        }

        return durationMs >= MIN_DURATION_MS && durationMs <= MAX_DURATION_MS
    }

    fun onUploadSpeechFile(uri: Uri) = intent {
        if (!validateSpeechFile(uri)) {
            postSideEffect(PracticeSideEffect.ShowSnackBar("발표 파일은 1분이상 20분 이하만 업로드 가능합니다."))
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadSpeechFile(uri.toString())
        }.onSuccess {
            Log.d("PracticeViewModel", "onUploadSpeechFile Success: $it")
        }.onFailure {
            Log.d("PracticeViewModel", "onUploadSpeechFile Failure: $it")
        }
    }

}


