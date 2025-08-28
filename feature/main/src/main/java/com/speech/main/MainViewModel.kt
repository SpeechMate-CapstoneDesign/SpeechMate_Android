package com.speech.main

import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.AuthRepository
import com.speech.practice.graph.practice.PracticeSideEffect
import com.speech.practice.graph.practice.PractieState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ContainerHost<Unit, MainSideEffect>, ViewModel() {
    override val container = container<Unit, MainSideEffect>(Unit)

    fun checkSession() = intent {
        suspendRunCatching {
            authRepository.checkSession()
        }.onSuccess {
            postSideEffect(MainSideEffect.NavigateToPractice)
        }.onFailure {
            postSideEffect(MainSideEffect.NavigateToLogin)
        }
    }


}