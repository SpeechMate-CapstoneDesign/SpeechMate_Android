package com.speech.auth.graph.login

import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.designsystem.R
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.container
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ContainerHost<Unit, LoginSideEffect>, ViewModel() {
    override val container = container<Unit, LoginSideEffect>(Unit)
    fun onIntent(event: LoginIntent) {
        when (event) {
            is LoginIntent.OnLoginClick -> loginKakao(event.idToken)
        }
    }

    fun loginKakao(idToken: String) = intent {
        suspendRunCatching {
            authRepository.loginKakao(idToken)
        }.onSuccess { isNewUser ->
            if (isNewUser) {
                postSideEffect(LoginSideEffect.NavigateToOnBoarding(idToken))
            } else {
                postSideEffect(LoginSideEffect.NavigateToPractice)
            }
        }.onFailure {
            postSideEffect(LoginSideEffect.ShowSnackBar("로그인에 실패했습니다."))
        }
    }
}
