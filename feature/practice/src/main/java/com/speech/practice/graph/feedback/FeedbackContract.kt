package com.speech.practice.graph.feedback

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechDetail

data class FeedbackState(
    val speechDetail: SpeechDetail = SpeechDetail(),
    val feedbackTab: FeedbackTab = FeedbackTab.SCRIPT,
    val playingState: PlayingState = PlayingState.Ready,
) : UiState

sealed class PlayingState {
    data object Ready : PlayingState()
    data object Playing : PlayingState()
    data object Paused : PlayingState()
}

sealed class FeedbackIntent : UiIntent {
    data object OnBackPressed : FeedbackIntent()
    data class OnTabSelected(val feedbackTab: FeedbackTab) : FeedbackIntent()
    data object StartPlaying : FeedbackIntent()
    data object PausePlaying : FeedbackIntent()
    data object ResumePlaying : FeedbackIntent()
}

sealed interface FeedbackSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : FeedbackSideEffect
    data object NavigateToBack : FeedbackSideEffect
}