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

    private val audioFilePath: String = requireNotNull(savedStateHandle["audioFilePath"])
    private val audioFile = File(audioFilePath)

    private lateinit var player: MediaPlayer

    private val _playingAudioState = MutableStateFlow<PlayingAudioState>(PlayingAudioState.Stopped)
    val playingAudioState: StateFlow<PlayingAudioState> = _playingAudioState.asStateFlow()

    private val _currentTime = MutableStateFlow(0L)

    private val _timeText = MutableStateFlow("00:00 . 00")
    val timeText: StateFlow<String> = _timeText.asStateFlow()

    private var timerJob: Job? = null

    init {
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

    fun onEvent(event : PlayAudioEvent) = viewModelScope.launch { _eventChannel.send(event) }

    private fun setPlayingAudioState(playingAudioState: PlayingAudioState) {
        _playingAudioState.value = playingAudioState
    }

    private fun playAudio() {
        audioFile.let { file ->
            player = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()

                    startTimer()
                } catch(e: Exception) {
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

    private fun setTimerText(currentTime: Long) {
        val m = (currentTime / 1000) / 60
        val s = (currentTime / 1000) % 60
        val ms = ((currentTime % 1000) / 10).toInt()
        _timeText.value = String.format(Locale.US, "%02d : %02d . %02d", m, s, ms)
    }

    private fun startTimer() {
        if (timerJob != null) return

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_playingAudioState.value == PlayingAudioState.Playing) {
                delay(10)
                _currentTime.value += 10
                if (_currentTime.value % 130L == 0L) {
                    setTimerText(_currentTime.value)
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