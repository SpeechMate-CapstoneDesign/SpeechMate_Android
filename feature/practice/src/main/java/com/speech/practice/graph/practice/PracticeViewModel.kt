package com.speech.practice.graph.practice

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(

) : ViewModel()  {
    private val _eventChannel = Channel<PracticeEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    sealed class PracticeEvent {

    }


}
