package com.speech.auth.graph

import androidx.lifecycle.ViewModel
import com.speech.common.event.EventHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    internal val eventHelper: EventHelper
) : ViewModel() {
    private val _eventChannel = Channel<LoginEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    sealed class LoginEvent {
        data object LoginSuccess : LoginEvent()
        data object LoginFailure : LoginEvent()
    }
}