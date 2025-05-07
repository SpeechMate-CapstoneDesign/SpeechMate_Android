package com.speech.practice.graph.recordaudio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class RecordAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _eventChannel = Channel<RecordAudioEvent>(Channel.BUFFERED)

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Ready)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _elapsedTime = MutableStateFlow(0L)

    private val _timeText = MutableStateFlow("00:00 . 00")
    val timeText: StateFlow<String> = _timeText.asStateFlow()

    private var timerJob: Job? = null
    private var recordJob: Job? = null

    private var totalBytes = 0
    private  var recorder: AudioRecord? = null
    private lateinit var audioFile: File

    init {
        _eventChannel.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is RecordAudioEvent.RecordingStarted -> recordAudio()
                    is RecordAudioEvent.RecordingStopped -> stopRecordAudio()
                    is RecordAudioEvent.RecordingCanceled -> cancelAudio()
                    is RecordAudioEvent.RecordingPaused -> pauseAudio()
                    is RecordAudioEvent.RecordingResumed -> resumeAudio()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: RecordAudioEvent) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    private fun setRecordingState(recordingState: RecordingState) {
        _recordingState.value = recordingState
    }

    private fun recordAudio() {
        if (_recordingState.value != RecordingState.Ready) return
        setRecordingState(RecordingState.Recording)

        audioFile = File(
            context.filesDir,
            "record_${System.currentTimeMillis()}.wav"
        )

        startTimer()

        startRecordingLoop(true)
    }

    private fun pauseAudio() {
        if (_recordingState.value != RecordingState.Recording) return
        setRecordingState(RecordingState.Paused)

        recorder?.apply { stop(); release() } // AudioRecrod에는 Pause기능이 따로 없기 때문에 recorder 해제
        recorder = null

        stopTimer()
    }

    private fun resumeAudio() {
        if (recordingState.value != RecordingState.Paused) return
        setRecordingState(RecordingState.Recording)

        startTimer()
        startRecordingLoop(false)
    }

    private fun startRecordingLoop(isFirstSegment: Boolean) {
        recordJob?.cancel()

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize
        ).apply { startRecording() }

        recordJob = viewModelScope.launch(Dispatchers.IO) {
            FileOutputStream(audioFile, !isFirstSegment).use { fos ->
                if (isFirstSegment) {
                    fos.write(ByteArray(44)) // WAV 헤더
                }
                val buffer = ByteArray(bufferSize)
                while (_recordingState.value == RecordingState.Recording) {
                    val read = recorder!!.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        fos.write(buffer, 0, read)
                        totalBytes += read
                    }
                }
            }
        }
    }

    private fun stopRecordAudio() {
        if (_recordingState.value != RecordingState.Recording && _recordingState.value != RecordingState.Paused) return
        setRecordingState(RecordingState.Completed)

        recordJob?.cancel()

        recorder?.apply {
            stop()
            release()
        }

        recorder = null

        _elapsedTime.value = 0L
        setTimerText(_elapsedTime.value)
        stopTimer()

        audioFile.let { writeWavHeader(it, totalBytes) }
    }

    private fun cancelAudio() {
        if (_recordingState.value != RecordingState.Recording && _recordingState.value != RecordingState.Paused) return
        setRecordingState(RecordingState.Ready)

        _elapsedTime.value = 0L
        setTimerText(_elapsedTime.value)
        stopTimer()

        recordJob?.cancel()
        recorder?.apply { stop(); release() }
        recorder = null

        audioFile.delete()
    }

    private fun startTimer() {
        if (timerJob != null) return

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_recordingState.value == RecordingState.Recording) {
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

    sealed class RecordingState {
        data object Ready : RecordingState()
        data object Recording : RecordingState()
        data object Paused : RecordingState()
        data object Completed : RecordingState()
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