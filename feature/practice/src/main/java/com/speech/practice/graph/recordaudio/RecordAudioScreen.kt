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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.speech.common.ui.SimpleCircle
import com.speech.common.ui.StrokeCircle
import com.speech.common.util.clickable
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.R
import com.speech.designsystem.theme.PrimaryActive
import com.speech.practice.graph.recordaudio.RecordAudioViewModel.RecordAudioEvent

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
        onEvent = viewModel::onEvent,
        elapsedTime = elapsedTime
    )
}

@Composable
private fun RecordAudioScreen(
    navigateBack: () -> Unit,
    onEvent : (RecordAudioEvent) -> Unit,
    isRecording: Boolean,
    isPaused: Boolean,
    elapsedTime: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 20.dp, top = 20.dp),
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
                        onEvent(RecordAudioEvent.RecordingCanceled)
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
                       onEvent(RecordAudioEvent.RecordingStopped)
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
                        if (!isPaused) onEvent(RecordAudioEvent.RecordingPaused) else onEvent(RecordAudioEvent.RecordingResumed)
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
                        contentDescription = if(!isPaused) "일시 정지" else "재개",
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
                    onEvent(RecordAudioEvent.RecordingStarted)
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
//                onEvent(RecordAudioEvent.PlaybackStarted)
//            })
//            Spacer(Modifier.width(30.dp))
//            Image(painter = painterResource(R.drawable.stop_audio), contentDescription = null, modifier = Modifier.clickable {
//                onEvent(RecordAudioEvent.PlaybackStopped)
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
        onEvent = {},
        elapsedTime = "00 : 00.00"
    )
}