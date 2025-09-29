package com.speech.practice.graph.feedback

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechDetail

data class FeedbackState(
    val speechDetail: SpeechDetail = SpeechDetail(),
    val feedbackTab: FeedbackTab = FeedbackTab.SCRIPT,
    val tabStates: Map<FeedbackTab, TabState> =
        FeedbackTab.entries
            .filterNot { it == FeedbackTab.SPEECH_CONFIG || it == FeedbackTab.SCRIPT }
            .associateWith { TabState() },
    val playingState: PlayingState = PlayingState.Ready,
    val playerState: PlayerState = PlayerState(),
    val showDropdownMenu: Boolean = false,
) : UiState

data class TabState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)

data class PlayerState(
    val playbackSpeed: Float = 1.0f,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
) {
    val progress: Float
        get() = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

    val formattedCurrentPosition: String
        get() = formatTime(currentPosition)

    val formattedDuration: String
        get() = formatTime(duration)

    private fun formatTime(time: Long): String {
        val totalSeconds = time / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

sealed class PlayingState {
    data object Ready : PlayingState()
    data object Loading : PlayingState()
    data object Playing : PlayingState()
    data object Paused : PlayingState()
    data object Error : PlayingState()
}

sealed class FeedbackIntent : UiIntent {
    data object OnBackPressed : FeedbackIntent()
    data class OnTabSelected(val feedbackTab: FeedbackTab) : FeedbackIntent()
    data object StartPlaying : FeedbackIntent()
    data object PausePlaying : FeedbackIntent()
    data class SeekTo(val position: Long) : FeedbackIntent()
    data class ChangePlaybackSpeed(val speed: Float) : FeedbackIntent()
    data object OnMenuClick : FeedbackIntent()
    data object OnDeleteClick : FeedbackIntent()
}

sealed interface FeedbackSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : FeedbackSideEffect
    data object NavigateToBack : FeedbackSideEffect
}
