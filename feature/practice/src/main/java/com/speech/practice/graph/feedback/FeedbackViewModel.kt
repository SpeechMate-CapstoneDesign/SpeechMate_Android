package com.speech.practice.graph.feedback

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.material3.rememberDrawerState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.toRoute
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.repository.SpeechRepository
import com.speech.navigation.PracticeGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val speechRepository: SpeechRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ContainerHost<FeedbackState, FeedbackSideEffect>, ViewModel() {
    override val container = container<FeedbackState, FeedbackSideEffect>(FeedbackState())

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer? get() = _exoPlayer

    private var progressJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            intent {
                reduce {
                    state.copy(playingState = if (isPlaying) PlayingState.Playing else PlayingState.Paused)
                }
            }

            if (isPlaying) {
                startProgressUpdate()
            } else {
                stopProgressUpdate()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                    intent {
                        reduce {
                            state.copy(
                                playingState = PlayingState.Ready,
                                playerState = state.playerState.copy(currentPosition = 0.seconds),
                            )
                        }
                    }
                }

                Player.STATE_READY -> {
                    val duration = _exoPlayer?.duration ?: 0
                    intent {
                        reduce {
                            state.copy(
                                playerState = state.playerState.copy(duration = duration.milliseconds),
                                playingState = PlayingState.Ready,
                            )
                        }
                    }
                }

                Player.STATE_BUFFERING -> {
                    intent {
                        reduce {
                            state.copy(playingState = PlayingState.Loading)
                        }
                    }
                }

                Player.STATE_ENDED -> {
                    stopProgressUpdate()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            intent {
                reduce {
                    state.copy(playingState = PlayingState.Error)
                }
            }
        }
    }

    private fun initializePlayer() {
        _exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(playerListener)
        }
    }

    init {
        val routeArgs: PracticeGraph.FeedbackRoute = savedStateHandle.toRoute()
        initializePlayer()
        loadMedia(routeArgs.fileUrl)

        intent {
            reduce {
                state.copy(
                    speechDetail = state.speechDetail.copy(
                        id = routeArgs.speechId,
                        fileUrl = routeArgs.fileUrl,
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
        if (container.stateFlow.value.speechDetail.speechFileType == SpeechFileType.VIDEO) {
            getVideoAnalysis()
        }
    }

    fun onIntent(event: FeedbackIntent) {
        when (event) {
            is FeedbackIntent.OnBackPressed -> onBackPressed()
            is FeedbackIntent.OnTabSelected -> onTabSelected(event.feedbackTab)
            is FeedbackIntent.StartPlaying -> startPlaying()
            is FeedbackIntent.PausePlaying -> pausePlaying()
            is FeedbackIntent.SeekTo -> seekTo(event.position)
            is FeedbackIntent.ChangePlaybackSpeed -> setPlaybackSpeed(event.speed)
            is FeedbackIntent.OnMenuClick -> onMenuClick()
            is FeedbackIntent.OnDeleteClick -> onDeleteClick()
        }
    }

    private fun onBackPressed() = intent {
        val isPlaying = state.playingState == PlayingState.Playing
        if (isPlaying) {
            pausePlaying()
        } else {
            clearResource()
            postSideEffect(FeedbackSideEffect.NavigateToBack)
        }
        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "on_back_pressed",
            properties = mutableMapOf("is_playing" to isPlaying),
        )
    }

    private fun onMenuClick() = intent {
        reduce {
            state.copy(showDropdownMenu = true)
        }

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "on_menu_click",
        )
    }

    private fun onDeleteClick() = intent {
        suspendRunCatching {
            speechRepository.deleteSpeech(state.speechDetail.id)
        }.onSuccess {
            postSideEffect(FeedbackSideEffect.NavigateToBack)

            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "delete_speech",
            )
        }.onFailure {
            postSideEffect(FeedbackSideEffect.ShowSnackbar("스피치 삭제에 실패했습니다."))
            errorHelper.logError(it)
        }
    }

    fun onDismissDropdownMenu() = intent {
        reduce {
            state.copy(showDropdownMenu = false)
        }
    }

    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressJob = viewModelScope.launch(Dispatchers.Main) {
            while (true) {
                _exoPlayer?.let { player ->
                    val currentPosition = player.currentPosition

                    intent {
                        reduce {
                            state.copy(playerState = state.playerState.copy(currentPosition = currentPosition.milliseconds))
                        }
                    }
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun onTabSelected(feedbackTab: FeedbackTab) = intent {
        reduce {
            state.copy(feedbackTab = feedbackTab)
        }
        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "select_tab",
            properties = mutableMapOf("tab" to feedbackTab.name),
        )
    }

    private fun startPlaying() = intent {
        _exoPlayer?.play()

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "start_playing",
            properties = mutableMapOf("current_position" to state.playerState.currentPosition.inWholeMilliseconds),
        )
    }

    private fun pausePlaying() = intent {
        _exoPlayer?.pause()

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "pause_playing",
            properties = mutableMapOf("current_position" to state.playerState.currentPosition.inWholeMilliseconds),
        )
    }

    fun seekTo(position: Long) = intent {
        _exoPlayer?.seekTo(position)
        reduce {
            state.copy(playerState = state.playerState.copy(currentPosition = position.milliseconds))
        }

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "seek_to",
            properties = mutableMapOf("position" to position),
        )
    }

    fun setPlaybackSpeed(speed: Float) = intent {
        _exoPlayer?.setPlaybackSpeed(speed)
        reduce {
            state.copy(playerState = state.playerState.copy(playbackSpeed = speed))
        }

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "set_playback_speed",
            properties = mutableMapOf("speed" to speed),
        )
    }

    private fun loadMedia(fieUrl: String) {
        _exoPlayer?.let { player ->
            val mediaItem = MediaItem.fromUri(fieUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
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
            getVerbalAnalysis()
        }.onFailure {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.SCRIPT to TabState(
                        isLoading = false,
                        isError = true,
                    )) + (FeedbackTab.SCRIPT_ANALYSIS to TabState(
                        isLoading = false,
                        isError = true,
                    )),
                    speechDetail = state.speechDetail.copy(
                        script = "대본을 불러오는데 실패했습니다.",
                    ),
                )
            }
            errorHelper.logError(it)
        }
    }

    private fun getScriptAnalysis() = intent {
        suspendRunCatching {
            speechRepository.getScriptAnalysis(state.speechDetail.id)
        }.onSuccess { scriptAnalysis ->
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.SCRIPT_ANALYSIS to TabState(
                        isLoading = false,
                        isError = false,
                    )),
                    speechDetail = state.speechDetail.copy(scriptAnalysis = scriptAnalysis),
                )
            }
        }.onFailure {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.SCRIPT_ANALYSIS to TabState(
                        isLoading = false,
                        isError = true,
                    )),
                )
            }
            errorHelper.logError(it)
        }
    }


    private fun getVerbalAnalysis() = intent {
        suspendRunCatching {
            speechRepository.getVerbalAnalysis(state.speechDetail.id)
        }.onSuccess {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.VERBAL_ANALYSIS to TabState(
                        isLoading = false,
                        isError = false,
                    )),
                    speechDetail = state.speechDetail.copy(
                        verbalAnalysis = it,
                    ),
                )
            }
        }.onFailure {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.VERBAL_ANALYSIS to TabState(
                        isLoading = false,
                        isError = true,
                    )),
                )
            }
            errorHelper.logError(it)
        }
    }

    private fun getVideoAnalysis() = intent {
        suspendRunCatching {

        }.onSuccess {

        }.onFailure {
            errorHelper.logError(it)
        }
    }

    fun clearResource() {
        _exoPlayer?.apply {
            stop()
            setVideoSurfaceView(null)
            clearVideoSurface()
            removeListener(playerListener)
            release()
        }

        _exoPlayer = null
        stopProgressUpdate()
    }

    override fun onCleared() {
        super.onCleared()
        clearResource()
    }
}
