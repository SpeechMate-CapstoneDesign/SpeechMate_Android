package com.speech.practice.graph.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.common_ui.ui.dialog.SpeechConfigDialog
import com.speech.common_ui.ui.dialog.UploadFileDialog
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.theme.LightGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.RecordAudio
import com.speech.designsystem.theme.RecordVideo
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
internal fun PracticeRoute(
    navigateToRecordAudio: () -> Unit,
    navigateToRecordVideo: () -> Unit,
    navigateToFeedback: (Int, String, SpeechFileType, SpeechConfig) -> Unit,
    viewModel: PracticeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is PracticeSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is PracticeSideEffect.NavigateToRecordAudio -> navigateToRecordAudio()
            is PracticeSideEffect.NavigateToRecordVideo -> navigateToRecordVideo()
            is PracticeSideEffect.NavigateToFeedback -> navigateToFeedback(
                sideEffect.speechId,
                sideEffect.fileUrl,
                sideEffect.speechFileType,
                state.speechConfig,
            )
        }
    }

    PracticeScreen(
        state = state,
        onRecordAudioClick = { viewModel.onIntent(PracticeIntent.OnRecordAudioClick) },
        onRecordVideoClick = { viewModel.onIntent(PracticeIntent.OnRecordVideoClick) },
        onUploadSpeechFile = { uri -> viewModel.onIntent(PracticeIntent.OnUploadSpeechFile(uri)) },
        onSpeechConfigChange = { viewModel.onIntent(PracticeIntent.OnSpeechConfigChange(it)) },
    )

    if (state.isUploadingFile && state.uploadFileStatus != null) {
        UploadFileDialog(status = state.uploadFileStatus!!)
    }
}

@Composable
private fun PracticeScreen(
    state: PracticeState,
    onRecordAudioClick: () -> Unit,
    onRecordVideoClick: () -> Unit,
    onUploadSpeechFile: (Uri) -> Unit,
    onSpeechConfigChange: (SpeechConfig) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(50.dp))

                    Image(
                        painter = painterResource(R.drawable.presenter),
                        contentDescription = "발표자",
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("발표를 연습하고", style = SpeechMateTheme.typography.headingMB)

                    Text(
                        text = buildAnnotatedString {
                            append("즉시 ")
                            withStyle(style = SpanStyle(color = PrimaryActive)) {
                                append("피드백")
                            }
                            append("을 받아보세요!")
                        },
                        style = SpeechMateTheme.typography.headingMB,
                    )

                    Spacer(Modifier.height(35.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(RecordAudio)
                                .padding(20.dp)
                                .clickable(
                                    onClick = rememberDebouncedOnClick {
                                        onRecordAudioClick()
                                    },
                                ),

                            ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(R.drawable.record_audio),
                                    contentDescription = "녹음",
                                )

                                Spacer(Modifier.width(6.dp))

                                Text("녹음", style = SpeechMateTheme.typography.bodyMM)
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(RecordVideo)
                                .padding(20.dp)
                                .clickable(
                                    onClick = rememberDebouncedOnClick {
                                        onRecordVideoClick()
                                    },
                                ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Image(
                                    modifier = Modifier.size(18.dp),
                                    painter = painterResource(R.drawable.record_video),
                                    contentDescription = "녹화",
                                )

                                Spacer(Modifier.width(6.dp))

                                Text("녹화", style = SpeechMateTheme.typography.bodyMM)
                            }
                        }

                        Spacer(Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(30.dp))

                    UploadFileButton(
                        onSpeechConfigChange = onSpeechConfigChange,
                        onUploadFile = onUploadSpeechFile,
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadFileButton(
    onSpeechConfigChange: (SpeechConfig) -> Unit,
    onUploadFile: (Uri) -> Unit,
) {
    var showSpeechConfigDg by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            selectedUri = uri
            showSpeechConfigDg = uri != null
        },
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LightGray)
            .padding(20.dp)
            .clickable {
                val mimeTypes = arrayOf("audio/*", "video/*")
                filePickerLauncher.launch(mimeTypes)
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.upload_file),
                contentDescription = "파일 업로드",
            )

            Spacer(Modifier.width(6.dp))

            Text("업로드", style = SpeechMateTheme.typography.bodyMM)
        }
    }

    if (showSpeechConfigDg && selectedUri != null) {
        SpeechConfigDialog(
            onDone = { speechConfig ->
                onSpeechConfigChange(speechConfig)
                onUploadFile(selectedUri!!)
                showSpeechConfigDg = false
                selectedUri = null
            },
            onDismiss = {
                showSpeechConfigDg = false
                selectedUri = null
            },
        )
    }
}


@Preview
@Composable
private fun PracticeScreenPreview() {
    PracticeScreen(
        state = PracticeState(),
        onRecordAudioClick = {},
        onRecordVideoClick = {},
        onUploadSpeechFile = {},
        onSpeechConfigChange = {},
    )
}
