package com.speech.practice.graph.practice

import android.Manifest
import android.app.Application
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var recorder: AudioRecord? = null
    private var audioFile: File? = null
    private var recordJob : Job? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun recordAudio() {
        if (_isRecording.value) return
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

        audioFile = File(
            context.filesDir,
            "record_\${System.currentTimeMillis()}.wav"
        )

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize
        ).apply { startRecording() }

        _isRecording.value = true

        viewModelScope.launch {
            _eventChannel.send(PracticeEvent.RecordingStarted)
        }

        viewModelScope.launch(Dispatchers.IO) {
            FileOutputStream(audioFile!!).use { fos ->
                // WAV 헤더 자리 확보 (44 bytes)
                fos.write(ByteArray(44))
                val buffer = ByteArray(bufferSize)
                var totalBytes = 0

                while (_isRecording.value) {
                    val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        fos.write(buffer, 0, read)
                        totalBytes += read
                    }
                }

            }
            _eventChannel.send(PracticeEvent.RecordingStopped)
        }


    }

    fun stopRecordAudio() {
        if (!_isRecording.value) return
        _isRecording.value = false
        recordJob?.cancel()

        recorder?.apply {
            stop()
            release()
        }

        recorder = null
    }


    sealed class PracticeEvent {
        data object RecordingStarted : PracticeEvent()
        data object RecordingStopped : PracticeEvent()
    }


    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }
}
