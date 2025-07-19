package com.speech.auth.graph.onboarding

import androidx.lifecycle.ViewModel
import com.speech.common.event.EventHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    internal val eventHelper: EventHelper,
) : ViewModel() {
    private val _eventChannel = Channel<OnBoardingEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()


    sealed class OnBoardingEvent {
        data object SignupSuccess : OnBoardingEvent()
        data object SignupFailure : OnBoardingEvent()
    }
}
