package com.speech.practice.graph.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.BackButton
import com.speech.common_ui.ui.SpeechMateTab
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.theme.LightGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFileType
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun FeedbackRoute(
    navigateToBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FeedbackSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is FeedbackSideEffect.NavigateToBack -> navigateToBack()
        }
    }

    FeedbackScreen(
        state = state,
        exoPlayer = viewModel.exoPlayer,
        onBackPressed = {
            viewModel.onIntent(FeedbackIntent.OnBackPressed)
        },
        onTabSelected = { tab ->
            viewModel.onIntent(FeedbackIntent.OnTabSelected(tab))
        },
        onStartPlaying = {
            viewModel.onIntent(FeedbackIntent.StartPlaying)
        },
        onPausePlaying = {
            viewModel.onIntent(FeedbackIntent.PausePlaying)
        },
        onSeekTo = { position ->
            viewModel.onIntent(FeedbackIntent.SeekTo(position))
        },
        onChangePlaybackSpeed = { speed ->
            viewModel.onIntent(FeedbackIntent.ChangePlaybackSpeed(speed))
        },
    )
}

@Composable
private fun FeedbackScreen(
    state: FeedbackState,
    exoPlayer: ExoPlayer?,
    onBackPressed: () -> Unit,
    onTabSelected: (FeedbackTab) -> Unit,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 55.dp),
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    PlayerSurface(
                        player = exoPlayer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 11f),
                    )

                    when (state.playingState) {
                        is PlayingState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = PrimaryActive,
                            )
                        }

                        is PlayingState.Error -> {
                            Text(
                                "영상 또는 음성 파일을 불러오는데 실패했습니다.",
                                modifier = Modifier.align(Alignment.Center),
                                color = Color.White,
                                style = SpeechMateTheme.typography.bodySM
                            )
                        }

                        else -> {}
                    }
                }

                Spacer(Modifier.height(8.dp))

                MediaControls(
                    state = state,
                    onStartPlaying = onStartPlaying,
                    onPausePlaying = onPausePlaying,
                    onSeekTo = onSeekTo,
                    onChangePlaybackSpeed = onChangePlaybackSpeed,
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FeedbackTab.entries.forEach { tab ->
                        if (state.speechDetail.speechFileType == SpeechFileType.AUDIO && tab == FeedbackTab.NON_VERBAL_ANALYSIS) return@forEach
                        SpeechMateTab(
                            label = tab.label,
                            isSelected = state.feedbackTab == tab,
                            onTabSelected = { onTabSelected(tab) },
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                when (state.feedbackTab) {
                    FeedbackTab.SPEECH_CONFIG -> {
                        val config = state.speechDetail.speechConfig
                        Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                            Text(
                                "날짜: ${state.speechDetail.fornattedTime}",
                                style = SpeechMateTheme.typography.bodyXMM,
                            )
                            Text(
                                "발표 이름: ${config.fileName}",
                                style = SpeechMateTheme.typography.bodyXMM,
                            )
                            Text(
                                "발표 상황: ${config.speechType!!.label}",
                                style = SpeechMateTheme.typography.bodyXMM,
                            )
                            Text(
                                "청중: ${config.audience!!.label}",
                                style = SpeechMateTheme.typography.bodyXMM,
                            )
                            Text(
                                "발표 장소: ${config.venue!!.label}",
                                style = SpeechMateTheme.typography.bodyXMM,
                            )
                        }
                    }

                    FeedbackTab.SCRIPT -> {
                        if (state.speechDetail.script.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Spacer(Modifier.height(100.dp))

                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = PrimaryDefault,
                                )

                                Spacer(Modifier.height(15.dp))

                                Text(
                                    "대본을 불러오는 중입니다.",
                                    style = SpeechMateTheme.typography.bodyXMM,
                                )
                            }
                        } else {
                            Text(text = state.speechDetail.script, style = SpeechMateTheme.typography.bodyXMM)
                        }
                    }

                    FeedbackTab.SCRIPT_ANALYSIS -> {
                        val scriptAnalysis = state.speechDetail.scriptAnalysis
                        if (scriptAnalysis == null) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Spacer(Modifier.height(100.dp))

                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = PrimaryDefault,
                                )

                                Spacer(Modifier.height(15.dp))

                                Text(
                                    "대본을 분석한 결과를 불러오는 중입니다.",
                                    style = SpeechMateTheme.typography.bodyXMM,
                                )
                            }
                        } else {
                            if (scriptAnalysis.isError) {
                                Text(
                                    "대본을 분석한 결과를 불러오는데 실패했습니다.",
                                    style = SpeechMateTheme.typography.bodyXMM,
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                                    val analysis = state.speechDetail.scriptAnalysis!!
                                    Text(
                                        text = "요약: ${analysis.summary}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "키워드: ${analysis.keywords}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "개선점: ${analysis.improvementPoints}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "논리적 일관성 점수: ${analysis.logicalCoherenceScore}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "피드백: ${analysis.feedback}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "점수 설명: ${analysis.scoreExplanation}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )

                                    Text(
                                        text = "예상 질문: ${analysis.expectedQuestions}",
                                        style = SpeechMateTheme.typography.bodyXMM,
                                    )
                                }
                            }
                        }
                    }

                    FeedbackTab.VERBAL_ANALYSIS -> {}

                    FeedbackTab.NON_VERBAL_ANALYSIS -> {}
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val debouncedOnBackPressed = rememberDebouncedOnClick { onBackPressed() }

        BackButton(onBackPressed = debouncedOnBackPressed)

        Spacer(Modifier.width(5.dp))

        Text(state.speechDetail.speechConfig.fileName, style = SpeechMateTheme.typography.headingSB)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaControls(
    state: FeedbackState,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(state.progress) {
        if (!isDragging) {
            sliderValue = state.progress
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isPlaying = state.playingState == PlayingState.Playing

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { if (isPlaying) onPausePlaying() else onStartPlaying() },
            ) {
                Icon(
                    painter = if (isPlaying) {
                        painterResource(R.drawable.pause_audio)
                    } else {
                        painterResource(R.drawable.play_audio)
                    },
                    contentDescription = if (isPlaying) "일시정지" else "재생",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.width(12.dp))

            Slider(
                value = sliderValue,
                onValueChange = {
                    isDragging = true
                    sliderValue = it
                },
                onValueChangeFinished = {
                    isDragging = false
                    val newPosition = (sliderValue * state.duration).toLong()
                    onSeekTo(newPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = PrimaryActive,
                    inactiveTrackColor = LightGray,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .shadow(elevation = 1.dp, shape = CircleShape)
                            .background(color = PrimaryActive, shape = CircleShape),
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = PrimaryActive,
                            inactiveTrackColor = LightGray,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                        ),
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(8.dp),
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(6.dp))

        Row {
            Text(
                text = state.formattedCurrentPosition,
                style = SpeechMateTheme.typography.bodySM,
            )

            Text(
                text = " / ${state.formattedDuration}",
                style = SpeechMateTheme.typography.bodySM,
            )
        }
    }
}

@Preview(showBackground = true, name = "발표 설정 탭")
@Composable
private fun FeedbackScreenSpeechConfigPreview() {
    FeedbackScreen(
        state = FeedbackState(
            feedbackTab = FeedbackTab.SPEECH_CONFIG,
            speechDetail = SpeechDetail(
                speechConfig = SpeechConfig(
                    fileName = "중간 발표 1",
                ),
            ),

            ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onChangePlaybackSpeed = {},
    )
}

@Preview(showBackground = true, name = "대본 탭")
@Composable
private fun FeedbackScreenScriptPreview() {
    FeedbackScreen(
        state = FeedbackState(
            feedbackTab = FeedbackTab.SCRIPT,
            speechDetail = SpeechDetail(
                speechConfig = SpeechConfig(
                    fileName = "중간 발표 1",
                ),
            ),
            currentPosition = 100000,
            duration = 200000,
        ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onChangePlaybackSpeed = {},
    )
}

@Preview(showBackground = true, name = "대본 분석 탭")
@Composable
private fun FeedbackScreenScriptAnalysisPreview() {
    FeedbackScreen(
        state = FeedbackState(
            feedbackTab = FeedbackTab.SCRIPT_ANALYSIS,
            speechDetail = SpeechDetail(
                speechConfig = SpeechConfig(
                    fileName = "중간 발표 1",
                ),
            ),
        ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onChangePlaybackSpeed = {},
    )
}

@Preview(showBackground = true, name = "언어적 분석 탭")
@Composable
private fun FeedbackScreenVerbalAnalysisPreview() {
    FeedbackScreen(
        state = FeedbackState(
            feedbackTab = FeedbackTab.VERBAL_ANALYSIS,
            speechDetail = SpeechDetail(
                speechConfig = SpeechConfig(
                    fileName = "중간 발표 1",
                ),
            ),
        ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onChangePlaybackSpeed = {},
    )
}

@Preview(showBackground = true, name = "비언어적 분석 탭")
@Composable
private fun FeedbackScreenNonVerbalAnalysisPreview() {
    FeedbackScreen(
        state = FeedbackState(
            feedbackTab = FeedbackTab.NON_VERBAL_ANALYSIS,
            speechDetail = SpeechDetail(
                speechConfig = SpeechConfig(
                    fileName = "중간 발표 1",
                ),
            ),
        ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onChangePlaybackSpeed = {},
    )
}
