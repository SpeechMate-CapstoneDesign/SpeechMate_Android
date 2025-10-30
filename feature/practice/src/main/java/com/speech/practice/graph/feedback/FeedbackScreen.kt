package com.speech.practice.graph.feedback

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.speech.common.util.formatDuration
import com.speech.common_ui.compositionlocal.LocalSetShouldApplyScaffoldPadding
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.component.BackButton
import com.speech.designsystem.component.SectionDivider
import com.speech.designsystem.component.SpeechMateTab
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.component.CheckCancelDialog
import com.speech.designsystem.component.SMDropDownMenu
import com.speech.designsystem.component.SMDropdownMenuItem
import com.speech.designsystem.component.SimpleCircle
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFileType
import com.speech.practice.graph.feedback.component.CustomScrollableTabRow
import com.speech.practice.graph.feedback.component.FeedbackPlayer
import com.speech.practice.graph.feedback.component.ScriptAnalysisContent
import com.speech.practice.graph.feedback.component.SpeechConfigContent
import com.speech.practice.graph.feedback.component.VerbalAnalysisContent
import kotlinx.coroutines.delay
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val setScaffoldPadding = LocalSetShouldApplyScaffoldPadding.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.onIntent(FeedbackIntent.OnAppBackground)
                }

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.initializePlayer()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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

    BackHandler(enabled = true) {
        viewModel.onIntent(FeedbackIntent.OnBackPressed)
    }

    DisposableEffect(state.isFullScreen) {
        setScaffoldPadding(!state.isFullScreen)
        onDispose {
            setScaffoldPadding(true)
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
        onSeekForward = {
            viewModel.onIntent(FeedbackIntent.OnSeekForward)
        },
        onSeekBackward = {
            viewModel.onIntent(FeedbackIntent.OnSeekBackward)
        },
        onProgressChanged = { position ->
            viewModel.onIntent(FeedbackIntent.OnProgressChanged(position))
        },
        onChangePlaybackSpeed = { speed ->
            viewModel.onIntent(FeedbackIntent.ChangePlaybackSpeed(speed))
        },
        onMenuClick = {
            viewModel.onIntent(FeedbackIntent.OnMenuClick)
        },
        onDeleteClick = {
            viewModel.onIntent(FeedbackIntent.OnDeleteClick)
        },
        onDismissDropDownMenu = viewModel::onDismissDropdownMenu,
        onFullScreenClick = {
            viewModel.onIntent(FeedbackIntent.OnFullScreenClick)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedbackScreen(
    state: FeedbackState,
    exoPlayer: ExoPlayer?,
    onBackPressed: () -> Unit,
    onTabSelected: (FeedbackTab) -> Unit,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
    onMenuClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissDropDownMenu: () -> Unit,
    onProgressChanged: (Long) -> Unit,
    onFullScreenClick: () -> Unit,
) {
    var showDeleteDg by remember { mutableStateOf(false) }
    if (showDeleteDg) {
        CheckCancelDialog(
            onCheck = {
                onDeleteClick()
            },
            onDismiss = { showDeleteDg = false },
            content = stringResource(R.string.delete_speech_confirmation),
        )
    }
    var headerHeightPx by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    if (state.isFullScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SmTheme.colors.black.copy(0.8f))
                .padding(horizontal = 40.dp),
            contentAlignment = Alignment.Center,
        ) {
            FeedbackPlayer(
                state = state,
                exoPlayer = exoPlayer,
                onStartPlaying = onStartPlaying,
                onPausePlaying = onPausePlaying,
                onSeekTo = onSeekTo,
                onSeekForward = onSeekForward,
                onSeekBackward = onSeekBackward,
                onProgressChanged = onProgressChanged,
                onFullScreenClick = onFullScreenClick,
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { headerHeightPx = it.height },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(onBackPressed = onBackPressed)

                Spacer(Modifier.width(5.dp))

                Text(
                    state.speechDetail.speechConfig.fileName,
                    style = SmTheme.typography.headingSB,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.weight(1f))

                Box {
                    Icon(
                        painter = painterResource(R.drawable.ic_menu),
                        contentDescription = "메뉴",
                        modifier = Modifier.clickable(isRipple = true) {
                            onMenuClick()
                        },
                        tint = SmTheme.colors.content,
                    )

                    SMDropDownMenu(
                        expanded = state.showDropdownMenu,
                        onDismiss = onDismissDropDownMenu,
                        alignment = Alignment.TopEnd,
                        offset = IntOffset(0, with(LocalDensity.current) { 16.dp.roundToPx() }),
                        items = listOf(
                            SMDropdownMenuItem(
                                labelRes = R.string.delete,
                                action = { showDeleteDg = true },
                            ),
                        ),
                    )
                }
            }

            Column(Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FeedbackPlayer(
                        state = state,
                        exoPlayer = exoPlayer,
                        onStartPlaying = onStartPlaying,
                        onPausePlaying = onPausePlaying,
                        onSeekTo = onSeekTo,
                        onSeekForward = onSeekForward,
                        onSeekBackward = onSeekBackward,
                        onProgressChanged = onProgressChanged,
                        onFullScreenClick = onFullScreenClick,
                    )
                }

                Spacer(Modifier.height(10.dp))
            }

            CustomScrollableTabRow(
                tabs = FeedbackTab.entries.filterNot {
                    state.speechDetail.speechFileType == SpeechFileType.AUDIO && it == FeedbackTab.NON_VERBAL_ANALYSIS
                },
                selectedTab = state.feedbackTab,
                onTabSelected = onTabSelected,
            )
        }


        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = with(density) { headerHeightPx.toDp() }),
            ) {
                item {
                    Column(Modifier.padding(horizontal = 20.dp)) {
                        Spacer(Modifier.height(15.dp))

                        when (state.feedbackTab) {
                            FeedbackTab.SPEECH_CONFIG -> {
                                SpeechConfigContent(
                                    date = state.speechDetail.formattedDate,
                                    speechConfig = state.speechDetail.speechConfig,
                                )
                            }

                            FeedbackTab.SCRIPT -> {
                                val scriptTab = state.tabStates[FeedbackTab.SCRIPT] ?: TabState()
                                if (scriptTab.isLoading) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Spacer(Modifier.height(100.dp))

                                        CircularProgressIndicator(
                                            modifier = Modifier.size(48.dp),
                                            color = SmTheme.colors.primaryLight,
                                        )

                                        Spacer(Modifier.height(15.dp))

                                        Text(
                                            stringResource(R.string.loading_script),
                                            style = SmTheme.typography.bodyXMM,
                                            color = SmTheme.colors.textPrimary,
                                        )
                                    }
                                } else if (scriptTab.isError) {
                                    Text(
                                        text = stringResource(R.string.failed_script),
                                        style = SmTheme.typography.bodyXMM,
                                        color = SmTheme.colors.textPrimary,
                                    )
                                } else {
                                    val sentences = state.speechDetail.script.sentences
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                    ) {
                                        sentences.forEach { (timestamp, sentence) ->
                                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                                Text(
                                                    text = formatDuration(timestamp),
                                                    style = SmTheme.typography.bodyXMM,
                                                    color = SmTheme.colors.primaryDefault,
                                                    modifier = Modifier.clickable {
                                                        onSeekTo(timestamp.inWholeMilliseconds)
                                                    },
                                                )

                                                Spacer(Modifier.width(5.dp))

                                                Text(text = sentence, style = SmTheme.typography.bodyXMM, color = SmTheme.colors.textPrimary)
                                            }
                                        }

                                    }

                                }
                            }

                            FeedbackTab.SCRIPT_ANALYSIS -> {
                                val scriptAnalysisTab = state.tabStates[FeedbackTab.SCRIPT_ANALYSIS] ?: TabState()

                                if (scriptAnalysisTab.isLoading) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Spacer(Modifier.height(100.dp))

                                        CircularProgressIndicator(
                                            modifier = Modifier.size(48.dp),
                                            color = SmTheme.colors.primaryLight,
                                        )

                                        Spacer(Modifier.height(15.dp))

                                        Text(
                                            stringResource(R.string.loading_script_analysis),
                                            style = SmTheme.typography.bodyXMM,
                                            color = SmTheme.colors.textPrimary,
                                        )
                                    }
                                } else if (scriptAnalysisTab.isError) {
                                    Text(
                                        stringResource(R.string.failed_script_analysis),
                                        style = SmTheme.typography.bodyXMM,
                                        color = SmTheme.colors.textPrimary,
                                    )
                                } else {
                                    ScriptAnalysisContent(state.speechDetail.scriptAnalysis)
                                }
                            }

                            FeedbackTab.VERBAL_ANALYSIS -> {
                                val verbalAnalysisTab = state.tabStates[FeedbackTab.VERBAL_ANALYSIS] ?: TabState()
                                if (verbalAnalysisTab.isLoading) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Spacer(Modifier.height(100.dp))

                                        CircularProgressIndicator(
                                            modifier = Modifier.size(48.dp),
                                            color = SmTheme.colors.primaryLight,
                                        )

                                        Spacer(Modifier.height(15.dp))

                                        Text(
                                            stringResource(R.string.loading_verbal_analysis),
                                            style = SmTheme.typography.bodyXMM,
                                            color = SmTheme.colors.textPrimary,
                                        )
                                    }
                                } else if (verbalAnalysisTab.isError) {
                                    Text(
                                        stringResource(R.string.failed_verbal_analysis),
                                        style = SmTheme.typography.bodyXMM,
                                        color = SmTheme.colors.textPrimary,
                                    )
                                } else {
                                    VerbalAnalysisContent(
                                        duration = state.playerState.duration,
                                        verbalAnalysis = state.speechDetail.verbalAnalysis,
                                        seekTo = onSeekTo,
                                    )
                                }
                            }

                            FeedbackTab.NON_VERBAL_ANALYSIS -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Spacer(Modifier.height(50.dp))

                                    Text(
                                        text = stringResource(R.string.non_verbal_analysis_preparation),
                                        style = SmTheme.typography.bodyXMM,
                                        color = SmTheme.colors.textPrimary,
                                    )
                                }

//                        val nonVerbalAnalysisTab = state.tabStates[FeedbackTab.NON_VERBAL_ANALYSIS] ?: TabState()
//                        if (nonVerbalAnalysisTab.isLoading) {
//                            Column(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalAlignment = Alignment.CenterHorizontally,
//                                verticalArrangement = Arrangement.Center,
//                            ) {
//                                Spacer(Modifier.height(100.dp))
//
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(48.dp),
//                                    color = SmTheme.colors.primaryLight,
//                                )
//
//                                Spacer(Modifier.height(15.dp))
//
//                                Text(
//                                    "비언어적 행동을 분석 중입니다.",
//                                    style = SmTheme.typography.bodyXMM,
//                                )
//                            }
//                        } else if (nonVerbalAnalysisTab.isError) {
//                            Text(
//                                "비언어적 을 분석한 결과를 불러오는데 실패했습니다.",
//                                style = SmTheme.typography.bodyXMM,
//                            )
//                        } else {
//                            NonVerbalAnalysisContent(state.speechDetail.nonverbalAnalysis)
//                        }
                            }
                        }

                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
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
        onSeekForward = {},
        onSeekBackward = {},
        onChangePlaybackSpeed = {},
        onMenuClick = {},
        onDeleteClick = {},
        onDismissDropDownMenu = {},
        onProgressChanged = {},
        onFullScreenClick = {},
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
        ),
        exoPlayer = null,
        onBackPressed = {},
        onTabSelected = {},
        onStartPlaying = {},
        onPausePlaying = {},
        onSeekTo = {},
        onSeekForward = {},
        onSeekBackward = {},
        onChangePlaybackSpeed = {},
        onMenuClick = {},
        onDeleteClick = {},
        onDismissDropDownMenu = {},
        onProgressChanged = {},
        onFullScreenClick = {},
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
        onSeekForward = {},
        onSeekBackward = {},
        onChangePlaybackSpeed = {},
        onMenuClick = {},
        onDeleteClick = {},
        onDismissDropDownMenu = {},
        onProgressChanged = {},
        onFullScreenClick = {},
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
        onSeekForward = {},
        onSeekBackward = {},
        onChangePlaybackSpeed = {},
        onMenuClick = {},
        onDeleteClick = {},
        onDismissDropDownMenu = {},
        onProgressChanged = {},
        onFullScreenClick = {},
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
        onSeekForward = {},
        onSeekBackward = {},
        onChangePlaybackSpeed = {},
        onMenuClick = {},
        onDeleteClick = {},
        onDismissDropDownMenu = {},
        onProgressChanged = {},
        onFullScreenClick = {},
    )
}
