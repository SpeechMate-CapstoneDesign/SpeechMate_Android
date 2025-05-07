package com.speech.practice.graph.playaudio

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class PlayAudioViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _eventChannel = Channel<PlayAudioEvent>(Channel.BUFFERED)

    private val audioFilePath: String = requireNotNull(savedStateHandle["audioFilePath"])
    private val audioFile: File = File(audioFilePath)

    private var player: MediaPlayer? = null

    init {
        _eventChannel.receiveAsFlow()
            .onEach { event ->
                when (event) {
                    is PlayAudioEvent.PlaybackStarted -> playAudio()
                    is PlayAudioEvent.PlaybackStopped -> stopPlayAudio()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event : PlayAudioEvent) = viewModelScope.launch { _eventChannel.send(event) }

    private fun playAudio() {
        audioFile.let { file ->
            player?.release()
            player = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                } catch(e: Exception) {
                    Log.e("RecordAudioError", "Error playing audio ${e}")
                    release()
                    player = null
                }
            }
        }
    }

    private fun stopPlayAudio() {
        player?.apply { stop(); release() }
        player = null
    }

    sealed class PlayAudioEvent {
        data object PlaybackStarted : PlayAudioEvent()
        data object PlaybackStopped : PlayAudioEvent()
    }

}