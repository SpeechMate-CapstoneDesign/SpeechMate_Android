package com.speech.mypage.graph.mypage

import androidx.lifecycle.ViewModel
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val speechRepository: SpeechRepository
) : ContainerHost<MyPageState, MyPageSideEffect>, ViewModel() {
    override val container = container<MyPageState, MyPageSideEffect>(MyPageState())

    fun onIntent(event: MyPageIntent) {
        when (event) {
            is MyPageIntent.OnSettingClick -> intent {
                postSideEffect(MyPageSideEffect.NavigateToSetting)
            }

            is MyPageIntent.OnSpeechClick -> intent {
                postSideEffect(MyPageSideEffect.NavigateToFeedback(event.speechId))
            }
        }
    }

}