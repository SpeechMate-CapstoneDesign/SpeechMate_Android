package com.speech.practice.graph.feedback

import androidx.core.util.TimeUtils.formatDuration
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechDetail
import com.speech.common.util.formatDuration
import com.speech.common.util.getProgress
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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
    val isFullScreen : Boolean = false,
) : UiState

data class TabState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)

data class PlayerState(
    val playbackSpeed: Float = 1.0f,
    val currentPosition: Duration = 0.seconds,
    val duration: Duration = 0.seconds,
) {
    val progress: Float
        get() = getProgress(currentPosition, duration)

    val formattedCurrentPosition: String
        get() = formatDuration(currentPosition)

    val formattedDuration: String by lazy { formatDuration(duration) }
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
    data object OnSeekForward : FeedbackIntent()
    data object OnSeekBackward : FeedbackIntent()
    data class OnProgressChanged(val position: Long) : FeedbackIntent()
    data class ChangePlaybackSpeed(val speed: Float) : FeedbackIntent()
    data object OnMenuClick : FeedbackIntent()
    data object OnDeleteClick : FeedbackIntent()
    data object OnFullScreenClick : FeedbackIntent()
}

sealed interface FeedbackSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : FeedbackSideEffect
    data object NavigateToBack : FeedbackSideEffect
}
