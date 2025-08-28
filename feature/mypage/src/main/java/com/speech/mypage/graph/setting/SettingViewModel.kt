package com.speech.mypage.graph.setting

import androidx.lifecycle.ViewModel
import com.speech.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ContainerHost<Unit, SettingSideEffect>, ViewModel() {
    override val container = container<Unit, SettingSideEffect>(Unit)
    fun onIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.OnBackPressed -> intent {
                postSideEffect(SettingSideEffect.NavigateToBack)
            }
            is SettingIntent.OnLogout -> onLogOut()
            is SettingIntent.OnUnRegister -> onUnRegister()
            is SettingIntent.OnPolicyClick -> intent {
                postSideEffect(SettingSideEffect.NavigateToPolicy)
            }

            is SettingIntent.OnInquiry -> intent {
                postSideEffect(SettingSideEffect.NavigateToInquiry)
            }
        }
    }

    fun onLogOut() = intent {

    }

    fun onUnRegister() = intent {

    }
}