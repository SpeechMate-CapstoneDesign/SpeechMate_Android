package com.speech.mypage.graph.setting

import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ContainerHost<SettingState, SettingSideEffect>, ViewModel() {
    override val container = container<SettingState, SettingSideEffect>(SettingState())
    fun onIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.OnBackPressed -> intent {
                postSideEffect(SettingSideEffect.NavigateToBack)
            }
            is SettingIntent.OnLogout -> onLogout()
            is SettingIntent.OnUnRegister -> onUnRegister()
            is SettingIntent.OnPolicyClick -> intent {
                postSideEffect(SettingSideEffect.NavigateToPolicy)
            }

            is SettingIntent.OnInquiry -> intent {
                postSideEffect(SettingSideEffect.NavigateToInquiry)
            }
        }
    }

    fun onLogout() = intent {
        reduce {
            state.copy(showLogoutDialog = true)
        }

        suspendRunCatching {
            authRepository.logOut()
        }.onSuccess {
            postSideEffect(SettingSideEffect.NavigateToLogin)
        }.onFailure {
            postSideEffect(SettingSideEffect.NavigateToLogin)
        }
    }

    fun onUnRegister() = intent {
        suspendRunCatching {
            authRepository.unRegisterUser()
        }.onSuccess {
            postSideEffect(SettingSideEffect.NavigateToLogin)
        }.onFailure {
            postSideEffect(SettingSideEffect.ShowSnackbar("회원탈퇴에 실패했습니다."))
        }
    }
}