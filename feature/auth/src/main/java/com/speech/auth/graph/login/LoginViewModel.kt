package com.speech.auth.graph.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.common_ui.event.EventHelper
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    internal val eventHelper: com.speech.common_ui.event.EventHelper,
) : ViewModel() {
    private val _eventChannel = Channel<LoginEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    fun loginKakao(idToken: String) = viewModelScope.launch {
        authRepository.loginKakao(idToken).onSuccess { isNewUser ->
            if (isNewUser) {
                _eventChannel.send(LoginEvent.NavigateToOnBoarding(idToken))
            } else {
                _eventChannel.send(LoginEvent.NavigateToPractice)
            }
        }.onFailure {
            _eventChannel.send(LoginEvent.LoginFailure)
        }
    }

    sealed class LoginEvent {
        data class NavigateToOnBoarding(val idToken: String) : LoginEvent()
        data object NavigateToPractice : LoginEvent()
        data object LoginFailure : LoginEvent()
    }
}