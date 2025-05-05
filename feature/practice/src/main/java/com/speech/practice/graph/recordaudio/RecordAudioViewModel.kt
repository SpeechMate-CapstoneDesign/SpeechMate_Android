package com.speech.practice.graph.recordaudio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RecordAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _eventChannel = Channel<RecordAudioEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private val _timeText = MutableStateFlow("00:00 . 00")
    val timeText: StateFlow<String> = _timeText.asStateFlow()

    private var timerJob: Job? = null

    private var recorder: AudioRecord? = null
    private var audioFile: File? = null
    private var recordJob: Job? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun recordAudio() {
        if (_isRecording.value) return
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

        audioFile = File(
            context.filesDir,
            "record_\test.wav"
        )

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize
        ).apply { startRecording() }

        _isRecording.value = true
        startTimer()
        viewModelScope.launch {
            _eventChannel.send(RecordAudioEvent.RecordingStarted)
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
            _eventChannel.send(RecordAudioEvent.RecordingStopped)
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
        stopTimer()
    }

    private fun startTimer() {
        if (timerJob != null) return

        _elapsedTime.value = 0L
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_isRecording.value) {
                delay(10)
                _elapsedTime.value += 10
                if (_elapsedTime.value % 130L == 0L) {
                    setTimerText(_elapsedTime.value)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    @SuppressLint("DefaultLocale")
    private fun setTimerText(elapsedTime: Long) {
        val m = (elapsedTime / 1000) / 60
        val s = (elapsedTime / 1000) % 60
        val ms = ((elapsedTime % 1000) / 10).toInt()
        _timeText.value = String.format("%02d : %02d . %02d", m, s, ms)
    }

    sealed class RecordAudioEvent {
        data object RecordingStarted : RecordAudioEvent()
        data object RecordingStopped : RecordAudioEvent()
        data object PlaybackStarted : RecordAudioEvent()
        data object PlaybackStopped : RecordAudioEvent()
    }

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

}