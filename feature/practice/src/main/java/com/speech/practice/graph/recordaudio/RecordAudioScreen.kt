package com.speech.practice.graph.recordaudio

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.BackButton
import com.speech.common_ui.ui.SimpleCircle
import com.speech.common_ui.ui.SpeechConfigDialog
import com.speech.common_ui.ui.StrokeCircle
import com.speech.common_ui.ui.StrokeRoundRectangle
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.R
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import kotlin.concurrent.timer

@Composable
internal fun RecordAudioRoute(
    navigateToFeedback: (Int, SpeechFileType, SpeechConfig) -> Unit,
    navigateBack: () -> Unit,
    viewModel: RecordAudioViewModel = hiltViewModel(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is RecordAudioSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is RecordAudioSideEffect.NavigateToBack -> navigateBack()
            is RecordAudioSideEffect.NavigateToFeedback -> {
                navigateToFeedback(sideEffect.speechId, SpeechFileType.AUDIO, state.speechConfig)
            }
        }
    }

    RecordAudioScreen(
        state = state,
        onBackPressed = {
            viewModel.onIntent(RecordAudioIntent.OnBackPressed)
        },
        onRequestFeedback = {
            viewModel.onIntent(RecordAudioIntent.OnRequestFeedback)
        },
        onStartRecording = {
            viewModel.onIntent(RecordAudioIntent.StartRecording)
        },
        onFinishRecording = {
            viewModel.onIntent(RecordAudioIntent.FinishRecording)
        },
        onCancelRecording = {
            viewModel.onIntent(RecordAudioIntent.CancelRecording)
        },
        onPauseRecording = {
            viewModel.onIntent(RecordAudioIntent.PauseRecording)
        },
        onResumeRecording = {
            viewModel.onIntent(RecordAudioIntent.ResumeRecording)
        },
        onSpeechConfigChange = {
            viewModel.onIntent(RecordAudioIntent.OnSpeechConfigChange(it))
        },
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RecordAudioScreen(
    state: RecordAudioState,
    onBackPressed: () -> Unit,
    onRequestFeedback: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onSpeechConfigChange: (SpeechConfig) -> Unit,
) {
    var showSpeechConfigDg by remember { mutableStateOf(false) }
    val micPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO,
    )
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        val debouncedOnBackPressed = rememberDebouncedOnClick { onBackPressed() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BackButton(onBackPressed = debouncedOnBackPressed)
            }

            Spacer(Modifier.weight(1f))

            Text(state.timeText, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Light))

            Spacer(Modifier.weight(1f))

            when (state.recordingAudioState) {
                is RecordingAudioState.Ready -> {
                    Box(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable(isRipple = true) {
                                if (micPermissionState.status.isGranted && micPermissionState.status.isGranted) {
                                    onStartRecording()
                                } else {
                                    micPermissionState.launchPermissionRequest()
                                    if (!micPermissionState.status.shouldShowRationale) { // '다시 묻지 않음' 상태일 때 앱 설정 열기
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null),
                                        )

                                        context.startActivity(intent)
                                    }
                                }

                            },
                    ) {
                        SimpleCircle(
                            modifier = Modifier
                                .align(Center)
                                .shadow(elevation = 4.dp, shape = CircleShape),
                        )

                        Image(
                            painter = painterResource(R.drawable.michrophone),
                            contentDescription = "녹음",
                            modifier = Modifier.align(
                                Center,
                            ),
                        )
                    }
                }


                is RecordingAudioState.Recording, is RecordingAudioState.Paused -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(isRipple = true) {
                                    onCancelRecording()
                                },
                        ) {
                            StrokeCircle(
                                color = PrimaryDefault,
                                modifier = Modifier.align(
                                    Center,
                                ),
                            )

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "취소",
                                modifier = Modifier.align(
                                    Center,
                                ),
                                tint = DarkGray,
                            )
                        }

                        Spacer(Modifier.width(30.dp))

                        Box(
                            modifier = Modifier
                                .clickable() {
                                    onFinishRecording()
                                },
                        ) {
                            StrokeCircle(
                                color = PrimaryDefault,
                                diameter = 70.dp,
                                modifier = Modifier.align(
                                    Center,
                                ),
                            )

                            Image(
                                painter = painterResource(R.drawable.stop_audio),
                                contentDescription = "정지",
                                modifier = Modifier
                                    .size(34.dp)
                                    .align(
                                        Center,
                                    ),
                                colorFilter = ColorFilter.tint(PrimaryActive),
                            )
                        }

                        Spacer(Modifier.width(30.dp))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(isRipple = true) {
                                    if (state.recordingAudioState == RecordingAudioState.Recording) onPauseRecording() else onResumeRecording()
                                },
                        ) {
                            StrokeCircle(
                                color = PrimaryDefault,
                                modifier = Modifier.align(
                                    Center,
                                ),
                            )

                            Image(
                                painter = if (state.recordingAudioState == RecordingAudioState.Recording) painterResource(
                                    R.drawable.pause_audio,
                                ) else painterResource(
                                    R.drawable.play_audio,
                                ),
                                contentDescription = if (state.recordingAudioState == RecordingAudioState.Recording) "일시 정지" else "재개",
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(
                                        Center,
                                    ),
                                colorFilter = ColorFilter.tint(DarkGray),
                            )
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }

                is RecordingAudioState.Completed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 60.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(PrimaryActive)
                            .clickable {
                                showSpeechConfigDg = true
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Center),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.feedback),
                                contentDescription = "피드백 받기",
                                modifier = Modifier
                                    .size(24.dp),
                                colorFilter = ColorFilter.tint(Color.White),
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                "피드백 받기",
                                style = SpeechMateTheme.typography.bodyMSB,
                                color = Color.White,
                            )
                        }

                    }

                    Spacer(Modifier.height(30.dp))

                    Box(
                        modifier = Modifier
                            .clickable {
                                onCancelRecording()
                            },
                    ) {
                        StrokeRoundRectangle(
                            modifier = Modifier
                                .align(Center),
                        )

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .align(Center),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.michrophone),
                                contentDescription = "재녹음",
                                modifier = Modifier
                                    .size(24.dp),
                                colorFilter = ColorFilter.tint(PrimaryActive),
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                "재녹음",
                                style = SpeechMateTheme.typography.bodyMM,
                                color = PrimaryActive,
                            )
                        }

                    }
                }
            }

            if (state.recordingAudioState == RecordingAudioState.Completed) Spacer(
                Modifier.weight(
                    1f,
                ),
            )
            else Spacer(Modifier.height(60.dp))
        }

        if (showSpeechConfigDg) {
            SpeechConfigDialog(
                onDone = { speechConfig ->
                    onSpeechConfigChange(speechConfig)
                    onRequestFeedback()
                },
                onDismiss = { showSpeechConfigDg = false },
            )
        }
    }

}

@Preview(name = "Ready", showBackground = true)
@Composable
private fun RecordAudioScreenReadyPreview() {
    SpeechMateTheme {
        RecordAudioScreen(
            state = RecordAudioState(
                recordingAudioState = RecordingAudioState.Ready,
                timeText = "00 : 00 . 00",
            ),
            onBackPressed = {},
            onRequestFeedback = {},
            onStartRecording = {},
            onFinishRecording = {},
            onCancelRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onSpeechConfigChange = {},
        )
    }
}

@Preview(name = "Recording", showBackground = true)
@Composable
private fun RecordAudioScreenRecordingPreview() {
    SpeechMateTheme {
        RecordAudioScreen(
            state = RecordAudioState(
                recordingAudioState = RecordingAudioState.Recording,
                timeText = "01 : 23 . 45",
            ),
            onBackPressed = {},
            onRequestFeedback = {},
            onStartRecording = {},
            onFinishRecording = {},
            onCancelRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onSpeechConfigChange = {},
        )
    }
}

@Preview(name = "Paused", showBackground = true)
@Composable
private fun RecordAudioScreenPausedPreview() {
    SpeechMateTheme {
        RecordAudioScreen(
            state = RecordAudioState(
                recordingAudioState = RecordingAudioState.Paused,
                timeText = "03 : 10 . 99",
            ),
            onBackPressed = {},
            onRequestFeedback = {},
            onStartRecording = {},
            onFinishRecording = {},
            onCancelRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onSpeechConfigChange = {},
        )
    }
}

@Preview(name = "Completed", showBackground = true)
@Composable
private fun RecordAudioScreenCompletedPreview() {
    SpeechMateTheme {
        RecordAudioScreen(
            state = RecordAudioState(
                recordingAudioState = RecordingAudioState.Completed,
                timeText = "05 : 00 . 00",
            ),
            onBackPressed = {},
            onRequestFeedback = {},
            onStartRecording = {},
            onFinishRecording = {},
            onCancelRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onSpeechConfigChange = {},
        )
    }
}
