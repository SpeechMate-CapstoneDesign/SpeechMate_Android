package com.speech.practice.graph.recordaudio

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.speech.common_ui.compositionlocal.LocalSetShouldApplyScaffoldPadding
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.component.BackButton
import com.speech.designsystem.component.SimpleCircle
import com.speech.practice.component.dialog.SpeechConfigDialog
import com.speech.designsystem.component.StrokeRoundRectangle
import com.speech.practice.component.dialog.UploadFileDialog
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.component.PrimaryIcon
import com.speech.designsystem.component.StrokeCircle
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.practice.graph.feedback.FeedbackIntent
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun RecordAudioRoute(
    navigateToFeedback: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    navigateBack: () -> Unit,
    viewModel: RecordAudioViewModel = hiltViewModel(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val state by viewModel.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.onIntent(RecordAudioIntent.OnAppBackground)
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
            is RecordAudioSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is RecordAudioSideEffect.NavigateToBack -> navigateBack()
            is RecordAudioSideEffect.NavigateToFeedback -> {
                navigateToFeedback(sideEffect.speechId, sideEffect.fileUrl, sideEffect.speechFileType, state.speechConfig)
            }
        }
    }

    BackHandler(enabled = true) {
        viewModel.onIntent(RecordAudioIntent.OnBackPressed)
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

    if (state.uploadFileStatus != null) {
        UploadFileDialog(status = state.uploadFileStatus!!)
    }
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
    val primaryGradient = Brush.verticalGradient(
        colors = listOf(SmTheme.colors.primaryGradientStart, SmTheme.colors.primaryGradientEnd),
    )

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

            Text(state.timeText, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Light), color = SmTheme.colors.textPrimary)

            Spacer(Modifier.weight(1f))

            when (state.recordingAudioState) {
                is RecordingAudioState.Ready -> {
                    PrimaryIcon(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(4.dp, shape = CircleShape)
                            .clickable {
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
                        shape = CircleShape,
                        icon = R.drawable.ic_mic,
                    )
                }


                is RecordingAudioState.Recording, is RecordingAudioState.Paused -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(30.dp))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onCancelRecording()
                                },
                        ) {
                            StrokeCircle(diameter = 48.dp)

                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "취소",
                                modifier = Modifier
                                    .align(Center),
                                tint = SmTheme.colors.content,
                            )
                        }


                        Spacer(Modifier.weight(1f))


                        PrimaryIcon(
                            modifier = Modifier
                                .shadow(4.dp, shape = CircleShape)
                                .clickable(isRipple = true) {
                                    if (state.recordingAudioState == RecordingAudioState.Recording) onPauseRecording() else onResumeRecording()
                                },
                            shape = CircleShape,
                            contentPadding = 32,
                            icon = if (state.recordingAudioState == RecordingAudioState.Recording)
                                R.drawable.ic_pause else R.drawable.ic_play,
                        )

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onFinishRecording()
                                },
                        ) {
                            StrokeCircle(diameter = 48.dp)

                            Icon(
                                painter = painterResource(R.drawable.ic_stop),
                                contentDescription = "정지",
                                modifier = Modifier
                                    .align(Center)
                                    .size(20.dp),
                                tint = SmTheme.colors.content,
                            )
                        }



                        Spacer(Modifier.width(30.dp))
                    }
                }

                is RecordingAudioState.Completed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 60.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(brush = primaryGradient)
                            .clickable {
                                showSpeechConfigDg = true
                            },
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Center),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_feedback),
                                contentDescription = "피드백 받기",
                                modifier = Modifier
                                    .size(24.dp),
                                tint = SmTheme.colors.white,
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                stringResource(R.string.get_feedback),
                                style = SmTheme.typography.bodyMSB,
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
                                painter = painterResource(R.drawable.ic_mic),
                                contentDescription = "재녹음",
                                modifier = Modifier
                                    .size(24.dp),
                                colorFilter = ColorFilter.tint(SmTheme.colors.primaryDefault),
                            )

                            Spacer(Modifier.width(6.dp))

                            Text(
                                stringResource(R.string.re_record_audio),
                                style = SmTheme.typography.bodyMM,
                                color = SmTheme.colors.primaryDefault,
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
