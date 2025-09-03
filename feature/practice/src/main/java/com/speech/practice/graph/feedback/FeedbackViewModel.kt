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
import com.speech.common.util.suspendRunCatching
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.ScriptAnalysis
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

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val speechRepository: SpeechRepository,
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
                                currentPosition = 0L,
                            )
                        }
                    }
                }

                Player.STATE_READY -> {
                    val duration = _exoPlayer?.duration ?: 0L
                    intent {
                        reduce {
                            state.copy(
                                duration = duration,
                                playingState = PlayingState.Ready,
                            )
                        }
                    }
                }

                Player.STATE_BUFFERING -> {
                    Log.d("FeedbackViewModel", "Playback state: BUFFERING")
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
        getAudioAnalysis()
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
        }
    }

    private fun onBackPressed() {
        val isPlaying = container.stateFlow.value.playingState == PlayingState.Playing
        if (isPlaying) pausePlaying()
        else {
            clearResource()
            intent {
                postSideEffect(FeedbackSideEffect.NavigateToBack)
            }
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
                            state.copy(currentPosition = currentPosition)
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
    }

    private fun startPlaying() {
        _exoPlayer?.play()
    }

    private fun pausePlaying() {
        _exoPlayer?.pause()
    }

    fun seekTo(position: Long) {
        _exoPlayer?.seekTo(position)
        intent {
            Log.d("FeedbackViewModel1", "seekTo - duration: ${state.duration}, currentPosition: $position")
            reduce {
                state.copy(currentPosition = position)
            }
            Log.d("FeedbackViewModel2", "seekTo - duration: ${state.duration}, currentPosition: $position")
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        _exoPlayer?.setPlaybackSpeed(speed)
        intent {
            reduce {
                state.copy(playbackSpeed = speed)
            }
        }
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
        }.onFailure {
            reduce {
                state.copy(
                    speechDetail = state.speechDetail.copy(
                        script = "대본을 불러오는데 실패했습니다.",
                        scriptAnalysis = state.speechDetail.scriptAnalysis?.copy(isError = true),
                    ),
                )
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
                        scriptAnalysis = state.speechDetail.scriptAnalysis?.copy(isError = true),
                    ),
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

     fun clearResource() {
        _exoPlayer?.clearVideoSurface()
        _exoPlayer?.removeListener(playerListener)
        _exoPlayer?.release()
        _exoPlayer = null
        stopProgressUpdate()
    }

    override fun onCleared() {
        super.onCleared()
        clearResource()
    }
}
