package com.speech.practice.graph.playaudio

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.practice.graph.recordaudio.RecordAudioViewModel.RecordingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject
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
import java.util.Locale

@HiltViewModel
class PlayAudioViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _eventChannel = Channel<PlayAudioEvent>(Channel.BUFFERED)

    private val _audioFilePath: String = requireNotNull(savedStateHandle["audioFilePath"])
    private val _audioFile = File(_audioFilePath)

    private lateinit var player: MediaPlayer

    private val _playingAudioState = MutableStateFlow<PlayingAudioState>(PlayingAudioState.Stopped)
    val playingAudioState: StateFlow<PlayingAudioState> = _playingAudioState.asStateFlow()

    private val _currentTime = MutableStateFlow(0L)

    private val _currentTimeText = MutableStateFlow("00:00 . 00")
    val currentTimeText = _currentTimeText.asStateFlow()

    var totalTimeText: String = "0분"

    private var timerJob: Job? = null

    init {
        getTotalTime(_audioFilePath)

        _eventChannel.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is PlayAudioEvent.PlayAudioStarted -> {
                        setPlayingAudioState(PlayingAudioState.Playing)
                        playAudio()
                    }

                    is PlayAudioEvent.PlayAudioStopped -> {
                        setPlayingAudioState(PlayingAudioState.Stopped)
                        stopPlayAudio()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: PlayAudioEvent) = viewModelScope.launch { _eventChannel.send(event) }

    private fun getTotalTime(audioFilePath: String) {
        try {
            MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
            }.let { mp ->
                val duration = mp.duration
                totalTimeText = getFormattedTotalTime(duration.toLong())
            }
        } catch (e: Exception) {
            Log.e("MediaInit", "Failed to get duration: $e")
        }
    }


    private fun setPlayingAudioState(playingAudioState: PlayingAudioState) {
        _playingAudioState.value = playingAudioState
    }

    private fun playAudio() {
        _audioFile.let { file ->
            player = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()

                    startTimer()
                } catch (e: Exception) {
                    Log.e("PlayAudioException", "Error playing audio ${e}")
                    release()
                }
            }
        }
    }

    private fun stopPlayAudio() {
        player.apply { stop(); release() }
        stopTimer()
    }

    private fun getFormattedTotalTime(time: Long): String {
        val totalSeconds = time / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val parts = mutableListOf<String>()
        if (hours > 0) parts.add("${hours}시간")
        if (minutes > 0) parts.add("${minutes}분")
        if (seconds > 0 || parts.isEmpty()) parts.add("${seconds}초")

        return parts.joinToString(" ")
    }


    private fun getFormattedTime(time: Long): String {
        val m = (time / 1000) / 60
        val s = (time / 1000) % 60
        val ms = ((time % 1000) / 10).toInt()
        return String.format(Locale.US, "%02d : %02d . %02d", m, s, ms)
    }


    private fun startTimer() {
        if (timerJob != null) return

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_playingAudioState.value == PlayingAudioState.Playing) {
                delay(10)
                _currentTime.value += 10
                if (_currentTime.value % 130L == 0L) {
                    _currentTimeText.value = getFormattedTime(_currentTime.value)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    sealed class PlayingAudioState {
        data object Playing : PlayingAudioState()
        data object Stopped : PlayingAudioState()
    }

    sealed class PlayAudioEvent {
        data object PlayAudioStarted : PlayAudioEvent()
        data object PlayAudioStopped : PlayAudioEvent()
    }

}