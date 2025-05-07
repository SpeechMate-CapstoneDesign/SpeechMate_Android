package com.speech.practice.graph.playaudio

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class PlayAudioViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _eventChannel = Channel<PlayAudioEvent>(Channel.BUFFERED)

    private val audioFilePath: String = requireNotNull(savedStateHandle["audioFilePath"])
    private val audioFile = File(audioFilePath)

    private lateinit var player: MediaPlayer

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
            player = MediaPlayer().apply {
                try {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                } catch(e: Exception) {
                    Log.e("PlayAudioException", "Error playing audio ${e}")
                    release()
                }
            }
        }
    }

    private fun stopPlayAudio() {
        player.apply { stop(); release() }
    }

    sealed class PlayAudioEvent {
        data object PlaybackStarted : PlayAudioEvent()
        data object PlaybackStopped : PlayAudioEvent()
    }

}