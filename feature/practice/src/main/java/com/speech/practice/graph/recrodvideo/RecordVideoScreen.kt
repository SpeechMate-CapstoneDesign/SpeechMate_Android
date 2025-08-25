package com.speech.practice.graph.recrodvideo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.theme.SpeechMateTheme
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

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
        startRecordVideo = {}
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordVideoScreen(
    state: RecordVideoState,
    startRecordVideo: () -> Unit = {},
) {
    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA
    )
    val micPermissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            when(state.recordingVideoState) {
                is RecordingVideoState.Ready -> {
                    Button(
                        onClick = {
                            if (cameraPermissionState.status.isGranted && micPermissionState.status.isGranted) {

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
                        }, shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("녹화", style = SpeechMateTheme.typography.headingMB)
                    }
                }

                is RecordingVideoState.Recording, is RecordingVideoState.Paused -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                        },
                        update = { previewView ->
                            viewModel.bindCamera(lifecycleOwner, previewView.surfaceProvider)
                        }
                    )
                }

                is RecordingVideoState.Completed -> {

                }
            }


        }

    }
}

@Preview(showBackground = true)
@Composable
fun RecordVideoScreenPreview() {
    RecordVideoScreen(state = RecordVideoState(), onRequestFailure = {}, startRecordVideo = {})
}
