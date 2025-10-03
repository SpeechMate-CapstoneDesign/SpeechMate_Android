package com.speech.practice.graph.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.practice.component.dialog.SpeechConfigDialog
import com.speech.practice.component.dialog.UploadFileDialog
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.component.PrimaryIcon
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.theme.SmTheme
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

    if (state.uploadFileStatus != null) {
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
                .padding(horizontal = 32.dp),
        ) {
            item {
                Spacer(Modifier.height(32.dp))

                Text(stringResource(R.string.home_title), style = SmTheme.typography.headingMB, color = SmTheme.colors.textPrimary)

                Spacer(Modifier.height(8.dp))

                Text(stringResource(R.string.home_sub_title), style = SmTheme.typography.bodyXMM, color = SmTheme.colors.textSecondary)

                Spacer(Modifier.height(40.dp))

                SMCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRecordAudioClick()
                        },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        PrimaryIcon(icon = R.drawable.ic_record_audio)

                        Spacer(Modifier.width(13.dp))

                        Column {
                            Text(
                                stringResource(R.string.record_audio),
                                style = SmTheme.typography.headingSB,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                stringResource(R.string.record_audio_description),
                                style = SmTheme.typography.bodyXSM,
                                color = SmTheme.colors.textSecondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                SMCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRecordVideoClick()
                        },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        PrimaryIcon(icon = R.drawable.ic_record_video)

                        Spacer(Modifier.width(13.dp))

                        Column {
                            Text(
                                stringResource(R.string.record_video),
                                style = SmTheme.typography.headingSB,
                                color = SmTheme.colors.textPrimary,
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                stringResource(R.string.record_video_description),
                                style = SmTheme.typography.bodyXSM,
                                color = SmTheme.colors.textSecondary,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                UploadFileButton(
                    onSpeechConfigChange = onSpeechConfigChange,
                    onUploadFile = onUploadSpeechFile,
                )
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

    SMCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val mimeTypes = arrayOf("audio/*", "video/*")
                filePickerLauncher.launch(mimeTypes)
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            PrimaryIcon(icon = R.drawable.ic_upload)

            Spacer(Modifier.width(13.dp))

            Column {
                Text(
                    stringResource(R.string.upload_file),
                    style = SmTheme.typography.headingSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    stringResource(R.string.upload_file_description),
                    style = SmTheme.typography.bodyXSM,
                    color = SmTheme.colors.textSecondary,
                )
            }
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
