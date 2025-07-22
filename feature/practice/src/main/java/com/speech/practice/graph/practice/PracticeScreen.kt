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
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.LightGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.RecordAudio
import com.speech.designsystem.theme.RecordVideo
import com.speech.designsystem.theme.SpeechMateTheme


@Composable
internal fun PracticeRoute(
    navigateToRecordAudio: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    PracticeScreen(
        navigateToRecordAudio = navigateToRecordAudio,
        onRecordVideo = {},
        onUploadFile = viewModel::onUploadFile
    )
}

@Composable
private fun PracticeScreen(
    navigateToRecordAudio: () -> Unit,
    onRecordVideo: () -> Unit,
    onUploadFile: (Uri) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(50.dp))

                Image(painter = painterResource(R.drawable.presenter), contentDescription = "발표자")

                Spacer(Modifier.height(10.dp))

                Text("발표를 연습하고", style = SpeechMateTheme.typography.headingMB)

                Text(text = buildAnnotatedString {
                    append("즉시 ")
                    withStyle(style = SpanStyle(color = PrimaryActive)) {
                        append("피드백")
                    }
                    append("을 받아보세요!")
                }, style = SpeechMateTheme.typography.headingMB)

                Spacer(Modifier.height(35.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(RecordAudio)
                            .padding(20.dp)
                            .clickable {
                                navigateToRecordAudio()
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.record_audio),
                                contentDescription = "녹음"
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
                            .clickable(isRipple = true) {
                                onRecordVideo()
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.record_video),
                                contentDescription = "녹음"
                            )

                            Spacer(Modifier.width(6.dp))

                            Text("녹화", style = SpeechMateTheme.typography.bodyMM)
                        }
                    }

                    Spacer(Modifier.weight(1f))
                }

                Spacer(Modifier.height(30.dp))

                UploadFileButton(onUploadFile = onUploadFile)
            }
        }
    }
}

@Composable
private fun UploadFileButton(onUploadFile: (Uri) -> Unit) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) onUploadFile(uri)
        }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(18.dp),
                painter = painterResource(R.drawable.upload_file),
                contentDescription = "파일 업로드"
            )

            Spacer(Modifier.width(6.dp))

            Text("업로드", style = SpeechMateTheme.typography.bodyMM)
        }
    }
}


@Preview
@Composable
private fun PracticeScreenPreview() {
    PracticeScreen(
        navigateToRecordAudio = {},
        onRecordVideo = {},
        onUploadFile = {}
    )
}