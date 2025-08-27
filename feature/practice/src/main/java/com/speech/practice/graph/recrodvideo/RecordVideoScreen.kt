package com.speech.practice.graph.recrodvideo

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.BackButton
import com.speech.common_ui.ui.SimpleCircle
import com.speech.common_ui.ui.SpeechConfigDialog
import com.speech.common_ui.ui.StrokeRoundRectangle
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.practice.graph.recordaudio.RecordingAudioState
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun RecordVideoRoute(
    navigateToFeedBack: (Int) -> Unit,
    navigateBack: () -> Unit,
    viewModel: RecordVideoViewModel = hiltViewModel(),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is RecordVideoSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is RecordVideoSideEffect.NavigateBack -> navigateBack()
            is RecordVideoSideEffect.NavigateToFeedback -> {
                navigateToFeedBack(sideEffect.speechId)
            }
        }
    }

    RecordVideoScreen(
        state = state,
        bindCamera = viewModel::bindCamera,
        onSwitchCamera = { viewModel.onIntent(RecordVideoIntent.SwitchCamera) },
        onStartRecording = { viewModel.onIntent(RecordVideoIntent.StartRecording) },
        onFinishRecording = { viewModel.onIntent(RecordVideoIntent.FinishRecording) },
        onPauseRecording = { viewModel.onIntent(RecordVideoIntent.PauseRecording) },
        onResumeRecording = { viewModel.onIntent(RecordVideoIntent.ResumeRecording) },
        onCancelRecording = { viewModel.onIntent(RecordVideoIntent.CancelRecording) },
        onRequestFeedback = { viewModel.onIntent(RecordVideoIntent.OnRequestFeedback) },
        onBackPressed = { viewModel.onIntent(RecordVideoIntent.OnBackPressed) },
        onSpeechConfigChange = { viewModel.onIntent(RecordVideoIntent.OnSpeechConfigChange(it)) }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordVideoScreen(
    state: RecordVideoState,
    bindCamera: (
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: androidx.camera.core.Preview.SurfaceProvider,
        cameraSelector: CameraSelector
    ) -> Unit,
    onSwitchCamera: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onRequestFeedback: () -> Unit,
    onBackPressed: () -> Unit,
    onSpeechConfigChange: (SpeechConfig) -> Unit
) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val micPermissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showSpeechConfigDg by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(50.dp))

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                }, update = { previewView ->
                    bindCamera(lifecycleOwner, previewView.surfaceProvider, state.cameraSelector)
                })
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(0.3f))
                .align(BottomCenter)
                .height(200.dp)

        )

        when (state.recordingVideoState) {
            is RecordingVideoState.Ready -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(BottomCenter)
                        .padding(bottom = 60.dp)
                ) {
                    RecordVideoButton(
                        modifier = Modifier.align(Center),
                        onClick = {
                            if (cameraPermissionState.status.isGranted && micPermissionState.status.isGranted) {
                                onStartRecording()
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                                micPermissionState.launchPermissionRequest()

                                if (!cameraPermissionState.status.shouldShowRationale || !micPermissionState.status.shouldShowRationale) { // '다시 묻지 않음' 상태일 때 앱 설정 열기
                                    val intent = Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", context.packageName, null)
                                    )
                                    context.startActivity(intent)
                                }
                            }
                        }
                    )


                    Box(
                        modifier = Modifier
                            .clickable {
                                onSwitchCamera()
                            }
                            .align(Alignment.CenterEnd)
                            .padding(end = 45.dp)
                    ) {
                        SimpleCircle(
                            diameter = 60.dp,
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Center)
                        )

                        Image(
                            painter = painterResource(R.drawable.switch_ic),
                            contentDescription = "카메라 전환",
                            modifier = Modifier.align(
                                Center
                            ),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }

            is RecordingVideoState.Recording, is RecordingVideoState.Paused -> {

            }

            is RecordingVideoState.Completed -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 60.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(PrimaryActive)
                        .clickable {
                            showSpeechConfigDg = true
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.feedback),
                            contentDescription = "피드백 받기",
                            modifier = Modifier
                                .size(24.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            "피드백 받기",
                            style = SpeechMateTheme.typography.bodyMSB,
                            color = Color.White
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
                            .align(Center)
                    )

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .align(Center),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.record_video),
                            contentDescription = "재녹화",
                            modifier = Modifier
                                .size(24.dp),
                            colorFilter = ColorFilter.tint(PrimaryActive)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            "재녹화",
                            style = SpeechMateTheme.typography.bodyMM,
                            color = PrimaryActive
                        )
                    }

                }
            }
        }

        if (showSpeechConfigDg) {
            SpeechConfigDialog(
                onDone = onSpeechConfigChange,
                onDismiss = { showSpeechConfigDg = false },
            )
        }
    }
}

@Composable
private fun RecordVideoButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape = CircleShape)
            .clickable(onClick = onClick)
    ) {
        SimpleCircle(
            color = Color.White,
            diameter = 80.dp,
            modifier = Modifier
                .align(Center)
                .shadow(elevation = 4.dp, shape = CircleShape)
        )

        SimpleCircle(
            color = PrimaryActive,
            diameter = 36.dp,
            modifier = Modifier
                .align(Center)
                .shadow(elevation = 4.dp, shape = CircleShape)
        )
    }
}


@Preview(name = "Ready", showBackground = true)
@Composable
private fun RecordVideoScreenReadyPreview() {
    SpeechMateTheme {
        RecordVideoScreen(
            state = RecordVideoState(recordingVideoState = RecordingVideoState.Ready),
            bindCamera = { _, _, _ -> },
            onSwitchCamera = {},
            onStartRecording = {},
            onFinishRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onRequestFeedback = {},
            onBackPressed = {},
            onSpeechConfigChange = {}
        )
    }
}

@Preview(name = "Recording", showBackground = true)
@Composable
private fun RecordVideoScreenRecordingPreview() {
    SpeechMateTheme {
        RecordVideoScreen(
            state = RecordVideoState(recordingVideoState = RecordingVideoState.Recording),
            bindCamera = { _, _, _ -> },
            onSwitchCamera = {},
            onStartRecording = {},
            onFinishRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onRequestFeedback = {},
            onBackPressed = {},
            onSpeechConfigChange = {}
        )
    }
}

@Preview(name = "Paused", showBackground = true)
@Composable
private fun RecordVideoScreenPausedPreview() {
    SpeechMateTheme {
        RecordVideoScreen(
            state = RecordVideoState(recordingVideoState = RecordingVideoState.Paused),
            bindCamera = { _, _, _ -> },
            onSwitchCamera = {},
            onStartRecording = {},
            onFinishRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onRequestFeedback = {},
            onBackPressed = {},
            onSpeechConfigChange = {}
        )
    }
}


@Preview(name = "Completed", showBackground = true)
@Composable
private fun RecordVideoScreenCompletedPreview() {
    SpeechMateTheme {
        RecordVideoScreen(
            state = RecordVideoState(recordingVideoState = RecordingVideoState.Completed),
            bindCamera = { _, _, _ -> },
            onSwitchCamera = {},
            onStartRecording = {},
            onFinishRecording = {},
            onPauseRecording = {},
            onResumeRecording = {},
            onCancelRecording = {},
            onRequestFeedback = {},
            onBackPressed = {},
            onSpeechConfigChange = {}
        )
    }
}