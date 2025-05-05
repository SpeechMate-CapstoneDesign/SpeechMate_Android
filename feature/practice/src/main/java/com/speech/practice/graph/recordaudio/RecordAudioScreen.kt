package com.speech.practice.graph.recordaudio

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.speech.common.ui.SimpleCircle
import com.speech.common.ui.StrokeCircle
import com.speech.common.util.clickable
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.LightGray
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.designsystem.R
import com.speech.designsystem.theme.PrimaryActive
import com.speech.practice.graph.practice.PracticeViewModel

@Composable
internal fun RecordAudioRoute(
    navigateBack: () -> Unit,
    viewModel: RecordAudioViewModel = hiltViewModel()
) {
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val isPaused by viewModel.isPaused.collectAsStateWithLifecycle()
    val elapsedTime by viewModel.timeText.collectAsStateWithLifecycle()

    RecordAudioScreen(
        navigateBack = navigateBack, isRecording = isRecording, isPaused = isPaused,
        onRecordAudio = viewModel::recordAudio,
        onPause = {},
        onPlay = {},
        onStop = viewModel::stopRecordAudio,
        onCancel = {},
        playAudio = viewModel::playAudio,
        stopPlayAudio = viewModel::stopPlayback,
        elapsedTime = elapsedTime
    )
}

@Composable
private fun RecordAudioScreen(
    navigateBack: () -> Unit,
    onRecordAudio: () -> Unit,
    onPause: () -> Unit,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    playAudio : () -> Unit,
    stopPlayAudio : () -> Unit,
    isRecording: Boolean,
    isPaused: Boolean,
    elapsedTime: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.size(50.dp)
                )
            }

            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Image(
                    painter = painterResource(R.drawable.setting_ic),
                    contentDescription = "설정",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(elapsedTime, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Light))

        Spacer(Modifier.weight(1f))

        if (isRecording) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier.clip(CircleShape).clickable(isRipple = true) {
                        onCancel()
                    }
                ) {
                    StrokeCircle(
                        color = PrimaryDefault,
                        modifier = Modifier.align(
                            Center
                        )
                    )

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "취소",
                        modifier = Modifier.align(
                            Center
                        ),
                        tint = DarkGray
                    )
                }

                Spacer(Modifier.width(30.dp))

                Box(
                    modifier = Modifier.clip(CircleShape).clickable(isRipple = true) {
                        onStop()
                    }
                ) {
                    StrokeCircle(
                        color = PrimaryDefault,
                        diameter = 70.dp,
                        modifier = Modifier.align(
                            Center
                        )
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
                    modifier = Modifier.clip(CircleShape).clickable(isRipple = true) {
                        if (!isPaused) onPause() else onPlay()
                    }
                ) {
                    StrokeCircle(
                        color = PrimaryDefault,
                        modifier = Modifier.align(
                            Center
                        )
                    )

                    Image(
                        painter = if (!isPaused) painterResource(R.drawable.pause_audio) else painterResource(
                            R.drawable.play_audio
                        ),
                        contentDescription = "일시 정지",
                        modifier = Modifier
                            .size(20.dp)
                            .align(
                                Center
                            ),
                        colorFilter = ColorFilter.tint(DarkGray)
                    )
                }

                Spacer(Modifier.weight(1f))

            }
        }


        if (!isRecording) {
            Box(
                modifier = Modifier.clip(shape = CircleShape).clickable(isRipple = true) {
                    onRecordAudio()
                }
            ) {
                SimpleCircle(
                    modifier = Modifier
                        .align(Center)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                )

                Image(
                    painter = painterResource(R.drawable.michrophone),
                    contentDescription = "녹음",
                    modifier = Modifier.align(
                        Center
                    )
                )
            }
        }

//        Row(modifier = Modifier.fillMaxWidth()) {
//            Image(painter = painterResource(R.drawable.play_audio), contentDescription = null, modifier = Modifier.clickable {
//                playAudio()
//            })
//            Spacer(Modifier.width(30.dp))
//            Image(painter = painterResource(R.drawable.stop_audio), contentDescription = null, modifier = Modifier.clickable {
//                stopPlayAudio()
//            })
//        }


        Spacer(Modifier.height(60.dp))

    }
}

@Preview
@Composable
private fun RecordAudioScreenPreview() {
    RecordAudioScreen(
        navigateBack = {},
        isRecording = true,
        isPaused = true,
        onRecordAudio = {},
        onPause = {},
        onPlay = {},
        onStop = {},
        onCancel = {},
        playAudio = {},
        stopPlayAudio = {},
        elapsedTime = "00 : 00.00"
    )
}