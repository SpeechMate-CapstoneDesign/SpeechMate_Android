package com.speech.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.common.util.ellipsize
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.repository.NotificationRepository
import com.speech.mypage.graph.mypage.MyPageSideEffect
import com.speech.mypage.graph.mypage.MyPageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ContainerHost<Unit, MainSideEffect>, ViewModel() {
    override val container = container<Unit, MainSideEffect>(Unit)

    init {
        intent {
            notificationRepository.notificationEvents.collect { event ->
                when (event) {
                    is NotificationRepository.NotificationEvent.NonVerbalCompleted -> {
                        val speechName = event.speechName.ellipsize(6)

                        postSideEffect(
                            MainSideEffect.ShowSnackbar(
                                "$speechName 비언어적 분석 완료!",
                            ),
                        )
                    }
                }
            }
        }
    }

    fun onIntent(event: MainIntent) {
        when (event) {
            is MainIntent.OnNotificationClick -> {
                if (event.type == NON_VERBAL_ANALYSIS) {
                    intent {
                        postSideEffect(MainSideEffect.NavigateToFeedback(event.speechId, tab = FeedbackTab.NON_VERBAL_ANALYSIS))
                    }
                }
            }
        }
    }

    companion object {
        const val NON_VERBAL_ANALYSIS = "non_verbal_analysis"
    }

}
