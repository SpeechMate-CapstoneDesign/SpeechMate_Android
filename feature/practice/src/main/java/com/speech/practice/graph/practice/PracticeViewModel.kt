package com.speech.practice.graph.practice

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.provider.OpenableColumns
import com.speech.common_ui.util.getExtension
import com.speech.domain.repository.SpeechRepository

@HiltViewModel
class PracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
) : ViewModel() {
    private val _eventChannel = Channel<PracticeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onUploadFile(uri: Uri) = viewModelScope.launch {
        val fileExtension = getExtension(context, uri)
        if(fileExtension.isNotEmpty()) {
            speechRepository.uploadFile(fileExtension)
        }
    }

    sealed class PracticeEvent {
        data object NavigateToRecordAudio : PracticeEvent()
        data object UploadFileSuccess : PracticeEvent()
        data object UploadFileFailure : PracticeEvent()
    }
}
