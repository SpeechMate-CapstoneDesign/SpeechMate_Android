package com.speech.practice.graph.practice

import android.Manifest
import android.app.Application
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _eventChannel = Channel<PracticeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onUploadFile(uri: Uri) {

    }

    sealed class PracticeEvent {
        data object NavigateToRecordAudio : PracticeEvent()
        data object UploadFileSuccess : PracticeEvent()
        data object UploadFileFailure : PracticeEvent()
    }
}
