package com.speech.auth.graph.login

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kakao.sdk.common.util.Utility
import com.speech.analytics.AnalyticsEvent
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.designsystem.R
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.viewmodel.container
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
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

            analyticsHelper.trackActionEvent(
                screenName = "login",
                actionName = "login_kakao",
                properties = mutableMapOf("newUser" to isNewUser),
            )
        }.onFailure {
            postSideEffect(LoginSideEffect.ShowSnackBar("로그인에 실패했습니다."))
            errorHelper.logError(it)
        }
    }
}
