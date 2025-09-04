package com.speech.practice.graph.recrodvideo

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.LockScreenOrientation
import com.speech.common_ui.ui.SimpleCircle
import com.speech.common_ui.ui.dialog.SpeechConfigDialog
import com.speech.common_ui.ui.dialog.UploadFileDialog
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun RecordVideoRoute(
    navigateToFeedback: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
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
                navigateToFeedback(sideEffect.speechId, sideEffect.fileUrl, sideEffect.speechFileType, state.speechConfig)
            }
        }
    }

    BackHandler(enabled = true) {
        viewModel.onIntent(RecordVideoIntent.OnBackPressed)
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
        onSpeechConfigChange = { viewModel.onIntent(RecordVideoIntent.OnSpeechConfigChange(it)) }
    )

    if(state.isUploadingFile) {
        UploadFileDialog()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordVideoScreen(
    state: RecordVideoState,
    bindCamera: (
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: androidx.camera.core.Preview.SurfaceProvider,
        cameraSelector: CameraSelector,
    ) -> Unit,
    onSwitchCamera: () -> Unit,
    onStartRecording: () -> Unit,
    onFinishRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onRequestFeedback: () -> Unit,
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
    val previewView = remember { PreviewView(context) }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    LaunchedEffect(state.cameraSelector) {
        bindCamera(
            lifecycleOwner,
            previewView.surfaceProvider,
            state.cameraSelector
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(16.dp)
                        )
                        .background(
                            if (state.recordingVideoState is RecordingVideoState.Recording) Color.Red else Color.Black.copy(
                                0.5f
                            )
                        )
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = state.timeText,
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }

            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { previewView }
            )
        }

        if (state.recordingVideoState is RecordingVideoState.Ready) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(0.3f))
                    .align(BottomCenter)
                    .height(160.dp)
            )
        }

        when (state.recordingVideoState) {
            is RecordingVideoState.Ready -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(BottomCenter)
                        .padding(bottom = 40.dp)
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

                    var rotationState by remember { mutableStateOf(0f) }

                    Box(
                        modifier = Modifier
                            .clickable {
                                rotationState += 360
                                onSwitchCamera()
                            }
                            .align(Alignment.CenterEnd)
                            .padding(end = 45.dp)
                    ) {
                        val rotationAngle by animateFloatAsState(
                            targetValue = rotationState,
                            animationSpec = tween(
                                durationMillis = 1500,
                                easing = LinearOutSlowInEasing
                            ),
                            label = "rotationAnimation"
                        )

                        SimpleCircle(
                            diameter = 40.dp,
                            color = Color.Black.copy(alpha = 0.4f),
                            modifier = Modifier
                                .align(Center)
                        )

                        Image(
                            painter = painterResource(R.drawable.switch_ic),
                            contentDescription = "카메라 전환",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier
                                .size(24.dp)
                                .align(
                                    Center
                                )
                                .rotate(rotationAngle)
                        )
                    }
                }
            }

            is RecordingVideoState.Recording, is RecordingVideoState.Paused -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(BottomCenter)
                        .padding(bottom = 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(isRipple = true) {
                                onCancelRecording()
                            }
                    ) {
                        SimpleCircle(
                            color = Color.White,
                            diameter = 50.dp,
                            modifier = Modifier
                                .align(Center)
                                .shadow(elevation = 4.dp, shape = CircleShape)
                        )

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "취소",
                            modifier = Modifier.align(
                                Center
                            ),
                            tint = Color.Black
                        )
                    }

                    Spacer(Modifier.width(30.dp))

                    Box(
                        modifier = Modifier
                            .clickable {
                                onFinishRecording()
                            }
                    ) {
                        SimpleCircle(
                            color = Color.White,
                            diameter = 70.dp,
                            modifier = Modifier
                                .align(Center)
                        )

                        Image(
                            painter = painterResource(R.drawable.stop_audio),
                            contentDescription = "정지",
                            modifier = Modifier
                                .size(34.dp)
                                .align(
                                    Center
                                ),
                            colorFilter = ColorFilter.tint(PrimaryActive)
                        )
                    }

                    Spacer(Modifier.width(30.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(isRipple = true) {
                                if (state.recordingVideoState == RecordingVideoState.Recording) onPauseRecording() else onResumeRecording()
                            }
                    ) {
                        SimpleCircle(
                            color = Color.White,
                            diameter = 50.dp,
                            modifier = Modifier
                                .align(Center)
                                .shadow(elevation = 4.dp, shape = CircleShape)
                        )

                        Image(
                            painter = if (state.recordingVideoState == RecordingVideoState.Recording) painterResource(
                                R.drawable.pause_audio
                            ) else painterResource(
                                R.drawable.play_audio
                            ),
                            contentDescription = if (state.recordingVideoState == RecordingVideoState.Recording) "일시 정지" else "재개",
                            modifier = Modifier
                                .size(20.dp)
                                .align(
                                    Center
                                ),
                            colorFilter = ColorFilter.tint(Color.Black)
                        )
                    }

                    Spacer(Modifier.weight(1f))
                }
            }

            is RecordingVideoState.Completed -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(BottomCenter)
                        .padding(bottom = 40.dp)
                ) {
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
                                .align(Center),
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
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 60.dp)
                            .clip(shape = RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable {
                                onCancelRecording()
                            }
                    ) {
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
        }

        if (showSpeechConfigDg) {
            SpeechConfigDialog(
                onDone = { speechConfig ->
                    onSpeechConfigChange(speechConfig)
                    showSpeechConfigDg = false
                    onRequestFeedback()
                },
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
            onSpeechConfigChange = {}
        )
    }
}
