package com.speech.practice.graph.feedback

import androidx.compose.material3.rememberDrawerState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.speech.common.util.suspendRunCatching
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.repository.SpeechRepository
import com.speech.navigation.PracticeGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val speechRepository: SpeechRepository,
) : ContainerHost<FeedbackState, FeedbackSideEffect>, ViewModel() {
    override val container = container<FeedbackState, FeedbackSideEffect>(FeedbackState())

    init {
        intent {
            val routeArgs: PracticeGraph.FeedbackRoute = savedStateHandle.toRoute()
            reduce {
                state.copy(
                    speechDetail = state.speechDetail.copy(
                        id = routeArgs.speechId,
                        speechFileType = routeArgs.speechFileType,
                        speechConfig = state.speechDetail.speechConfig.copy(
                            fileName = routeArgs.fileName,
                            speechType = routeArgs.speechType,
                            audience = routeArgs.audience,
                            venue = routeArgs.venue,
                        ),
                    ),
                )
            }
        }

        getScript()
        getAudioAnalysis()
        if (container.stateFlow.value.speechDetail.speechFileType == SpeechFileType.VIDEO) {
            getVideoAnalysis()
        }
    }

    fun onIntent(event: FeedbackIntent) {
        when (event) {
            is FeedbackIntent.OnBackPressed -> intent {
                postSideEffect(FeedbackSideEffect.NavigateToBack)
            }

            is FeedbackIntent.OnTabSelected -> onTabSelected(event.feedbackTab)
            is FeedbackIntent.StartPlaying -> startPlaying()
            is FeedbackIntent.PausePlaying -> pausePlaying()
            is FeedbackIntent.ResumePlaying -> resumePlaying()
        }
    }

    private fun onTabSelected(feedbackTab: FeedbackTab) = intent {
        reduce {
           state.copy(feedbackTab = feedbackTab)
        }
    }

    private fun startPlaying() = intent {
        reduce {
            state.copy(playingState = PlayingState.Playing)
        }
    }

    private fun pausePlaying() = intent {
        reduce {
            state.copy(playingState = PlayingState.Paused)
        }
    }

    private fun resumePlaying() = intent {
        reduce {
            state.copy(playingState = PlayingState.Playing)
        }
    }

    private fun getScript() = intent {
        suspendRunCatching {
            speechRepository.getScript(state.speechDetail.id)
        }.onSuccess {
            reduce {
                state.copy(speechDetail = state.speechDetail.copy(script = it))
            }

            getScriptAnalysis()
        }.onFailure {
            reduce {
                state.copy(speechDetail = state.speechDetail.copy(script = "대본을 불러오는데 실패했습니다."))
                state.copy(speechDetail = state.speechDetail.copy(script = "대본을 분석한 결과를 불러오는데 실패했습니다."))
            }
        }
    }

    private fun getScriptAnalysis() = intent {
        suspendRunCatching {
            speechRepository.getScriptAnalysis(state.speechDetail.id)
        }.onSuccess {
            reduce {
                state.copy(speechDetail = state.speechDetail.copy(scriptAnalysis = it))
            }
        }.onFailure {
            reduce {
                state.copy(
                    speechDetail = state.speechDetail.copy(
                        scriptAnalysis = (state.speechDetail.scriptAnalysis ?: ScriptAnalysis(
                            summary = "",
                            keywords = "",
                            improvementPoints = "",
                            logicalCoherenceScore = 0,
                            feedback = "",
                            scoreExplanation = "",
                            expectedQuestions = ""
                        )).copy(isError = true)
                    )
                )
            }
        }
    }

    private fun getAudioAnalysis() = intent {
        suspendRunCatching {

        }.onSuccess {

        }.onFailure {

        }
    }

    private fun getVideoAnalysis() = intent {
        suspendRunCatching {

        }.onSuccess {

        }.onFailure {

        }
    }
}
