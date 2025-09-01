package com.speech.practice.graph.feedback

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.BackButton
import com.speech.common_ui.ui.SpeechMateTab
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.practice.graph.recordaudio.RecordAudioViewModel
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
        onResumePlaying = {
            viewModel.onIntent(FeedbackIntent.ResumePlaying)
        },
    )
}

@Composable
private fun FeedbackScreen(
    state: FeedbackState,
    onBackPressed: () -> Unit,
    onTabSelected: (FeedbackTab) -> Unit,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onResumePlaying: () -> Unit,
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
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FeedbackTab.entries.forEach { tab ->
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

                        Text("날짜: ${state.speechDetail.foramttedTime}", style = SpeechMateTheme.typography.bodySM)

                        Spacer(Modifier.height(15.dp))

                        Text("발표 이름: ${config.fileName}", style = SpeechMateTheme.typography.bodySM)

                        Spacer(Modifier.height(15.dp))

                        Text("발표 상황: ${config.speechType!!.label}", style = SpeechMateTheme.typography.bodySM)

                        Spacer(Modifier.height(15.dp))

                        Text("청중: ${config.audience!!.label}", style = SpeechMateTheme.typography.bodySM)

                        Spacer(Modifier.height(15.dp))

                        Text("발표 장소: ${config.venue!!.label}", style = SpeechMateTheme.typography.bodySM)

                        Spacer(Modifier.height(15.dp))
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
                                    style = SpeechMateTheme.typography.bodySM,
                                )
                            }
                        } else {
                            Text(text = state.speechDetail.script, style = SpeechMateTheme.typography.bodySM)
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
                                    style = SpeechMateTheme.typography.bodySM,
                                )
                            }
                        } else {
                            if (scriptAnalysis.isError) {
                                Text(
                                    "대본을 분석한 결과를 불러오는데 실패했습니다.",
                                    style = SpeechMateTheme.typography.bodySM,
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                                    val analysis = state.speechDetail.scriptAnalysis!!
                                    Text(
                                        text = "요약: ${analysis.summary}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "키워드: ${analysis.keywords}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "개선점: ${analysis.improvementPoints}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "논리적 일관성 점수: ${analysis.logicalCoherenceScore}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "피드백: ${analysis.feedback}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "점수 설명: ${analysis.scoreExplanation}",
                                        style = SpeechMateTheme.typography.bodySM,
                                    )

                                    Text(
                                        text = "예상 질문: ${analysis.expectedQuestions}",
                                        style = SpeechMateTheme.typography.bodySM,
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


@Preview(showBackground = true, name = "발표 설정 탭")
@Composable
private fun FeedbackScreenSpeechConfigPreview() {
    SpeechMateTheme {
        FeedbackScreen(
            state = FeedbackState(
                feedbackTab = FeedbackTab.SPEECH_CONFIG,
                speechDetail = SpeechDetail(
                    speechConfig = SpeechConfig(
                        fileName = "중간 발표 1",
                    ),
                ),
            ),
            onBackPressed = {},
            onTabSelected = {},
            onStartPlaying = {},
            onPausePlaying = {},
            onResumePlaying = {},
        )
    }
}

@Preview(showBackground = true, name = "대본 탭")
@Composable
private fun FeedbackScreenScriptPreview() {
    SpeechMateTheme {
        FeedbackScreen(
            state = FeedbackState(
                feedbackTab = FeedbackTab.SCRIPT,
                speechDetail = SpeechDetail(
                    speechConfig = SpeechConfig(
                        fileName = "중간 발표 1",
                    ),
                ),
            ),
            onBackPressed = {},
            onTabSelected = {},
            onStartPlaying = {},
            onPausePlaying = {},
            onResumePlaying = {},
        )
    }
}

@Preview(showBackground = true, name = "대본 분석 탭")
@Composable
private fun FeedbackScreenScriptAnalysisPreview() {
    SpeechMateTheme {
        FeedbackScreen(
            state = FeedbackState(
                feedbackTab = FeedbackTab.SCRIPT_ANALYSIS,
                speechDetail = SpeechDetail(
                    speechConfig = SpeechConfig(
                        fileName = "중간 발표 1",
                    ),
                ),
            ),
            onBackPressed = {},
            onTabSelected = {},
            onStartPlaying = {},
            onPausePlaying = {},
            onResumePlaying = {},
        )
    }
}

@Preview(showBackground = true, name = "언어적 분석 탭")
@Composable
private fun FeedbackScreenVerbalAnalysisPreview() {
    SpeechMateTheme {
        FeedbackScreen(
            state = FeedbackState(
                feedbackTab = FeedbackTab.VERBAL_ANALYSIS,
                speechDetail = SpeechDetail(
                    speechConfig = SpeechConfig(
                        fileName = "중간 발표 1",
                    ),
                ),
            ),
            onBackPressed = {},
            onTabSelected = {},
            onStartPlaying = {},
            onPausePlaying = {},
            onResumePlaying = {},
        )
    }
}

@Preview(showBackground = true, name = "비언어적 분석 탭")
@Composable
private fun FeedbackScreenNonVerbalAnalysisPreview() {
    SpeechMateTheme {
        FeedbackScreen(
            state = FeedbackState(
                feedbackTab = FeedbackTab.NON_VERBAL_ANALYSIS,
                speechDetail = SpeechDetail(
                    speechConfig = SpeechConfig(
                        fileName = "중간 발표 1",
                    ),
                ),
            ),
            onBackPressed = {},
            onTabSelected = {},
            onStartPlaying = {},
            onPausePlaying = {},
            onResumePlaying = {},
        )
    }
}
