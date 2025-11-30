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
import com.speech.domain.model.speech.AnalysisStatus
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.repository.NotificationRepository
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
    private val notificationRepository: NotificationRepository,
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
                    val videoSize = _exoPlayer?.videoSize

                    intent {
                        val isPortrait = if (videoSize == null || videoSize.width <= 0 || videoSize.height <= 0) {
                            false
                        } else {
                            (videoSize.width < videoSize.height) && state.speechDetail.speechFileType == SpeechFileType.VIDEO
                        }

                        reduce {
                            state.copy(
                                playerState = state.playerState.copy(
                                    duration = duration.milliseconds,
                                    isPortrait = isPortrait,
                                ),
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

    init {
        val routeArgs: PracticeGraph.FeedbackRoute = savedStateHandle.toRoute()
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
                    feedbackTab = routeArgs.tab,
                )
            }

            if (state.speechDetail.speechFileType == SpeechFileType.AUDIO) {
                reduce {
                    state.copy(playerState = state.playerState.copy(isPortrait = false))
                }
            }

            if (state.speechDetail.fileUrl.isEmpty()) {
                getSpeechConfig()
            }
            getScript()
            if (state.speechDetail.speechFileType == SpeechFileType.VIDEO) {
                getNonverbalAnalysis()
            }

            subscribeNotifications()
        }
    }

    fun onIntent(event: FeedbackIntent) {
        when (event) {
            is FeedbackIntent.OnBackPressed -> onBackPressed()
            is FeedbackIntent.OnTabSelected -> onTabSelected(event.feedbackTab)
            is FeedbackIntent.StartPlaying -> startPlaying()
            is FeedbackIntent.PausePlaying -> pausePlaying()
            is FeedbackIntent.SeekTo -> seekTo(event.position)
            is FeedbackIntent.OnSeekForward -> seekForward()
            is FeedbackIntent.OnSeekBackward -> seekBackward()
            is FeedbackIntent.ChangePlaybackSpeed -> setPlaybackSpeed(event.speed)
            is FeedbackIntent.OnProgressChanged -> onProgressChanged(event.position)
            is FeedbackIntent.OnMenuClick -> onMenuClick()
            is FeedbackIntent.OnDeleteClick -> onDeleteClick()
            is FeedbackIntent.OnFullScreenClick -> onFullScreenClick()
            is FeedbackIntent.OnAppBackground -> onAppBackground()
        }
    }

    fun subscribeNotifications() = intent {
        notificationRepository.notificationEvents.collect { event ->
            when (event) {
                is NotificationRepository.NotificationEvent.NonVerbalCompleted -> {
                    if (event.speechId == state.speechDetail.id) {
                        getNonverbalAnalysis()
                    } else {
                        postSideEffect(FeedbackSideEffect.ShowSnackbar("${event.speechName} 비언어적 분석 완료!"))
                    }
                }
            }
        }
    }

    fun initializePlayer() {
        if (_exoPlayer != null) clearResource()

        val currentState = container.stateFlow.value
        val fileUrl = currentState.speechDetail.fileUrl
        val mediaItem = MediaItem.fromUri(fileUrl)
        val currentPosition = currentState.playerState.currentPosition

        _exoPlayer = ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(SEEK_INTERVAL)
            .setSeekForwardIncrementMs(
                SEEK_INTERVAL,
            ).build().apply {
                addListener(playerListener)
                setMediaItem(mediaItem)
                prepare()
                seekTo(currentPosition.inWholeMilliseconds)
            }
    }

    private fun onBackPressed() {
        val currentState = container.stateFlow.value
        val isFullScreen = currentState.isFullScreen
        val isPlaying = currentState.playingState == PlayingState.Playing
        if (isFullScreen) {
            intent {
                reduce { state.copy(isFullScreen = false) }
            }
        } else if (isPlaying) {
            pausePlaying()
        } else {
            clearResource()
            intent {
                postSideEffect(FeedbackSideEffect.NavigateToBack)
            }
        }
        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "on_back_pressed",
            properties = mutableMapOf("is_playing" to isPlaying),
        )
    }

    private fun onAppBackground() {
        _exoPlayer?.pause()
        _exoPlayer?.release()
        stopProgressUpdate()
        _exoPlayer = null
    }

    private fun onFullScreenClick() = intent {
        reduce {
            state.copy(isFullScreen = !state.isFullScreen)
        }

        analyticsHelper.trackActionEvent(
            screenName = "feedback",
            actionName = "on_full_screen_click",
            properties = mutableMapOf("is_full_screen" to state.isFullScreen),
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

    private fun startPlaying() {
        _exoPlayer?.play()

        intent {
            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "start_playing",
                properties = mutableMapOf("current_position" to state.playerState.currentPosition.inWholeMilliseconds),
            )
        }
    }

    private fun pausePlaying() {
        _exoPlayer?.pause()

        intent {
            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "pause_playing",
                properties = mutableMapOf("current_position" to state.playerState.currentPosition.inWholeMilliseconds),
            )
        }
    }

    fun seekTo(position: Long) {
        if (position < 0 || position > container.stateFlow.value.playerState.duration.inWholeMilliseconds) return
        _exoPlayer?.seekTo(position)

        intent {
            reduce {
                state.copy(playerState = state.playerState.copy(currentPosition = position.milliseconds))
            }

            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "seek_to",
                properties = mutableMapOf("position" to position),
            )
        }
    }

    fun seekForward() {
        _exoPlayer?.seekForward()
        val newPosition = _exoPlayer?.currentPosition ?: return

        intent {
            reduce {
                state.copy(playerState = state.playerState.copy(currentPosition = newPosition.milliseconds))
            }

            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "seek_forward",
                properties = mutableMapOf("position" to state.playerState.currentPosition.inWholeMilliseconds),
            )
        }
    }

    fun seekBackward() {
        _exoPlayer?.seekBack()
        val newPosition = _exoPlayer?.currentPosition ?: return

        intent {
            reduce {
                state.copy(playerState = state.playerState.copy(currentPosition = newPosition.milliseconds))
            }

            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "seek_backward",
                properties = mutableMapOf("position" to state.playerState.currentPosition.inWholeMilliseconds),
            )
        }
    }

    fun onProgressChanged(position: Long) = intent {
        reduce {
            state.copy(playerState = state.playerState.copy(currentPosition = position.milliseconds))
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _exoPlayer?.setPlaybackSpeed(speed)

        intent {
            reduce {
                state.copy(playerState = state.playerState.copy(playbackSpeed = speed))
            }

            analyticsHelper.trackActionEvent(
                screenName = "feedback",
                actionName = "set_playback_speed",
                properties = mutableMapOf("speed" to speed),
            )
        }
    }

    private fun getSpeechConfig() = intent {
        val response = speechRepository.getSpeechConfig(state.speechDetail.id)
        reduce {
            state.copy(
                speechDetail = state.speechDetail.copy(
                    createdAt = response.createdAt,
                    speechFileType = response.speechFileType,
                    fileUrl = response.fileUrl,
                    speechConfig = response.speechConfig,
                ),
            )
        }
    }


    private fun getScript() = intent {
        suspendRunCatching {
            speechRepository.getScript(state.speechDetail.id)
        }.onSuccess {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.SCRIPT to TabState(
                        isLoading = false,
                        isError = false,
                    )),
                    speechDetail = state.speechDetail.copy(script = it),
                )

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
                    )) + (FeedbackTab.VERBAL_ANALYSIS to TabState(
                        isLoading = false,
                        isError = true,
                    )),
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

    private fun getNonverbalAnalysis() = intent {
        suspendRunCatching {
            speechRepository.getNonVerbalAnalysis(state.speechDetail.id)
        }.onSuccess { nonVerbalAnalysis ->
            Log.d("nonVerbalAnalysis", "$nonVerbalAnalysis")
            if (nonVerbalAnalysis.status == AnalysisStatus.COMPLETED) {
                reduce {
                    state.copy(
                        speechDetail = state.speechDetail.copy(
                            nonVerbalAnalysis = nonVerbalAnalysis,
                        ),
                        tabStates = state.tabStates + (FeedbackTab.NON_VERBAL_ANALYSIS to TabState(
                            isLoading = false,
                            isError = false,
                        )),
                    )
                }
            }
        }.onFailure {
            reduce {
                state.copy(
                    tabStates = state.tabStates + (FeedbackTab.NON_VERBAL_ANALYSIS to TabState(
                        isLoading = false,
                        isError = true,
                    )),
                )
            }
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

    companion object {
        const val SEEK_INTERVAL = 10000L
    }
}
