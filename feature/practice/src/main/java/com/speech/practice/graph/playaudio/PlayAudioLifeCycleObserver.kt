package com.speech.practice.graph.playaudio

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal class PlayAudioLifecycleObserver(
    private val onPauseAudio: () -> Unit,
    private val onStopAudio: () -> Unit
) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> onPauseAudio()
            Lifecycle.Event.ON_STOP -> onStopAudio()
            else -> {}
        }
    }
}
