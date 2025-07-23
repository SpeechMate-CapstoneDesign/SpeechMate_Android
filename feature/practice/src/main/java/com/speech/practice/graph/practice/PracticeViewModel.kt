package com.speech.practice.graph.practice

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
) : ViewModel() {
    private val _eventChannel = Channel<PracticeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onUploadSpeechFile(uri: Uri) = viewModelScope.launch {
        speechRepository.uploadSpeechFile(uri.toString()).onSuccess {
            _eventChannel.send(PracticeEvent.UploadFileSuccess)
            Log.d("PracticeViewModel", "onUploadSpeechFile Success: $it")
        }.onFailure {
            _eventChannel.send(PracticeEvent.UploadFileFailure)
            Log.d("PracticeViewModel", "onUploadSpeechFile Failure: $it")
        }
    }

    sealed class PracticeEvent {
        data object NavigateToRecordAudio : PracticeEvent()
        data object NavigateToRecordVideo : PracticeEvent()
        data object UploadFileSuccess : PracticeEvent()
        data object UploadFileFailure : PracticeEvent()
    }
}
