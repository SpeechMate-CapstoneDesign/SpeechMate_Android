package com.speech.practice.graph.recordaudio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
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
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _eventChannel = Channel<RecordAudioEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)

    private val _timeText = MutableStateFlow("00:00 . 00")
    val timeText: StateFlow<String> = _timeText.asStateFlow()

    private var timerJob: Job? = null
    private var recordJob: Job? = null

    private var totalBytes = 0
    private var recorder: AudioRecord? = null
    private var audioFile: File? = null


    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun onEvent(event: RecordAudioEvent) {
        when (event) {
            is RecordAudioEvent.RecordingStarted -> recordAudio()
            is RecordAudioEvent.RecordingStopped -> stopRecordAudio()
            is RecordAudioEvent.RecordingCanceled -> cancelAudio()
            is RecordAudioEvent.RecordingPaused -> pauseAudio()
            is RecordAudioEvent.RecordingResumed -> resumeAudio()
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun recordAudio() {
        if (_isRecording.value) return

        audioFile = File(
            context.filesDir,
            "record_${System.currentTimeMillis()}.wav"
        )

        _isRecording.value = true
        startTimer()

        startRecordingLoop(true)
    }

    private fun pauseAudio() {
        if (!_isRecording.value || _isPaused.value) return
        _isPaused.value = true

        recorder?.apply { stop(); release() }
        recorder = null

        stopTimer()
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun resumeAudio() {
        if (!_isRecording.value || !_isPaused.value) return
        _isPaused.value = false

        startTimer()
        startRecordingLoop(false)
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecordingLoop(isFirstSegment: Boolean) {
        recordJob?.cancel()

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize
        ).apply { startRecording() }

        recordJob = viewModelScope.launch(Dispatchers.IO) {
            FileOutputStream(audioFile!!, !isFirstSegment).use { fos ->
                if (isFirstSegment) {
                    fos.write(ByteArray(44)) // WAV 헤더
                }
                val buffer = ByteArray(bufferSize)
                while (_isRecording.value && !_isPaused.value) {
                    val read = recorder?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        fos.write(buffer, 0, read)
                        totalBytes += read
                    }
                }
            }
        }
    }

    private fun stopRecordAudio() {
        if (!_isRecording.value) return
        _isRecording.value = false
        _isPaused.value = false

        recordJob?.cancel()

        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        _elapsedTime.value = 0L
        setTimerText(_elapsedTime.value)
        stopTimer()

        audioFile?.let { writeWavHeader(it, totalBytes) }
    }

    private fun cancelAudio() {
        if (!_isRecording.value) return

        _elapsedTime.value = 0L
        setTimerText(_elapsedTime.value)
        stopTimer()

        _isRecording.value = false
        _isPaused.value = false

        recordJob?.cancel()
        recorder?.apply { stop(); release() }
        recorder = null

        audioFile?.delete()
    }

    private fun startTimer() {
        if (timerJob != null) return

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


    private fun setTimerText(elapsedTime: Long) {
        val m = (elapsedTime / 1000) / 60
        val s = (elapsedTime / 1000) % 60
        val ms = ((elapsedTime % 1000) / 10).toInt()
        _timeText.value = String.format(Locale.US, "%02d : %02d . %02d", m, s, ms)
    }

    private fun writeWavHeader(file: File, totalAudioLen: Int) {
        val totalDataLen = totalAudioLen + 36
        val channels = 1
        val byteRate = SAMPLE_RATE * channels * 2
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
            .put("RIFF".toByteArray())
            .putInt(totalDataLen)
            .put("WAVE".toByteArray())
            .put("fmt ".toByteArray())
            .putInt(16)
            .putShort(1.toShort())
            .putShort(channels.toShort())
            .putInt(SAMPLE_RATE)
            .putInt(byteRate)
            .putShort((channels * 2).toShort())
            .putShort(16.toShort())
            .put("data".toByteArray())
            .putInt(totalAudioLen)
        RandomAccessFile(file, "rw").use { raf ->
            raf.seek(0)
            raf.write(header.array())
        }
    }


    sealed class RecordAudioEvent {
        data object RecordingStarted : RecordAudioEvent()
        data object RecordingPaused : RecordAudioEvent()
        data object RecordingResumed : RecordAudioEvent()
        data object RecordingStopped : RecordAudioEvent()
        data object RecordingCanceled : RecordAudioEvent()
    }

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

}