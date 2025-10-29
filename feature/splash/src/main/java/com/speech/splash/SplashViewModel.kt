package com.speech.splash

import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ContainerHost<Unit, SplashSideEffect>, ViewModel() {
    override val container = container<Unit, SplashSideEffect>(Unit)

    init {
        checkSession()
    }

    private fun checkSession() = intent {
        suspendRunCatching {
            authRepository.checkSession()
            delay(1000)
        }.onSuccess {
            postSideEffect(SplashSideEffect.NavigateToPractice)
        }.onFailure {
            postSideEffect(SplashSideEffect.NavigateToLogin)
        }
    }
}
