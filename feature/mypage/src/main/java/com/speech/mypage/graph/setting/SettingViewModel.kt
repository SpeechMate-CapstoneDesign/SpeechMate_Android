package com.speech.mypage.graph.setting

import androidx.lifecycle.ViewModel
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ContainerHost<Unit, SettingSideEffect>, ViewModel() {
    override val container = container<Unit, SettingSideEffect>(Unit)
    fun onIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.OnBackPressed -> intent {
                postSideEffect(SettingSideEffect.NavigateToBack)
            }

            is SettingIntent.OnLogout -> onLogout()

            is SettingIntent.OnUnRegisterUser -> onUnRegisterUser()

            SettingIntent.OnPolicyClick -> intent {
                postSideEffect(SettingSideEffect.NavigateToPolicy)
            }

            is SettingIntent.OnInquiry -> intent {
                postSideEffect(SettingSideEffect.NavigateToInquiry)
            }
        }
    }


    fun onLogout() = intent {
        suspendRunCatching {
            authRepository.logout()
        }.onSuccess {
            postSideEffect(SettingSideEffect.NavigateToLogin)
            analyticsHelper.trackActionEvent(
                screenName = "setting",
                actionName = "sign_out",
            )
        }.onFailure {
            postSideEffect(SettingSideEffect.NavigateToLogin)
            errorHelper.logError(it)
        }
    }

    fun onUnRegisterUser() = intent {
        suspendRunCatching {
            authRepository.unRegisterUser()
        }.onSuccess {
            postSideEffect(SettingSideEffect.NavigateToLogin)
            analyticsHelper.trackActionEvent(
                screenName = "setting",
                actionName = "un_register",
            )
        }.onFailure {
            postSideEffect(SettingSideEffect.ShowSnackbar("회원탈퇴에 실패했습니다."))
            errorHelper.logError(it)
        }
    }
}
