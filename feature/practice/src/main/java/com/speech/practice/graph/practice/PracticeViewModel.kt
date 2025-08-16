package com.speech.practice.graph.practice

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
) : ContainerHost<Unit, PracticeSideEffect>, ViewModel() {
    override val container = container<Unit, PracticeSideEffect>(Unit)

    fun onIntent(event: PracticeIntent) = intent {
        when (event) {
            is PracticeIntent.OnUploadSpeechFile -> onUploadSpeechFile(event.uri)
            is PracticeIntent.OnRecordAudioClick -> {
                postSideEffect(PracticeSideEffect.NavigateToRecordAudio)
            }
        }
    }

    fun onUploadSpeechFile(uri: Uri) = intent {
        suspendRunCatching {
            speechRepository.uploadSpeechFile(uri.toString())
        }.onSuccess {
            Log.d("PracticeViewModel", "onUploadSpeechFile Success: $it")
        }.onFailure {
            Log.d("PracticeViewModel", "onUploadSpeechFile Failure: $it")
        }
    }
}


