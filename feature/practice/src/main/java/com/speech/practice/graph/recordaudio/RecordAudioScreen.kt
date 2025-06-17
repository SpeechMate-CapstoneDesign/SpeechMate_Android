package com.speech.practice.graph.recordaudio

import android.hardware.lights.Light
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
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.speech.common.event.SpeechMateEvent
import com.speech.common.ui.SimpleCircle
import com.speech.common.ui.StrokeCircle
import com.speech.common.ui.StrokeRoundRectangle
import com.speech.common.util.clickable
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.R
import com.speech.designsystem.theme.LightGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.practice.graph.recordaudio.RecordAudioViewModel.RecordAudioEvent
import com.speech.practice.graph.recordaudio.RecordAudioViewModel.NavigationEvent
import com.speech.practice.graph.recordaudio.RecordAudioViewModel.RecordingState

@Composable
internal fun RecordAudioRoute(
    viewModel: RecordAudioViewModel = hiltViewModel(),
    navigateToPlayAudio: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()
    val elapsedTime by viewModel.timeText.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationChannel.collect { event ->
            when (event) {
                is NavigationEvent.NavigateBack -> navigateBack()
                is NavigationEvent.NavigateToPlayAudio -> {
                    navigateToPlayAudio(event.audioFilePath)
                }
            }
        }
    }

    RecordAudioScreen(
        recordingState = recordingState,
        elapsedTime = elapsedTime,
        onEvent = viewModel::onEvent,
        navigateBack = { viewModel.onNavigationEvent(NavigationEvent.NavigateBack) },
        navigateToPlayAudio = viewModel::navigateToPlayAudio

    )
}

@Composable
private fun RecordAudioScreen(
    onEvent: (RecordAudioEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToPlayAudio: () -> Unit,
    recordingState: RecordingState,
    elapsedTime: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp),
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
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(elapsedTime, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Light))

        Spacer(Modifier.weight(1f))

        if (recordingState == RecordingState.Recording || recordingState == RecordingState.Paused) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(isRipple = true) {
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
                    modifier = Modifier
                        .clickable() {
                            onEvent(RecordAudioEvent.RecordingFinished)
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
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(isRipple = true) {
                            if (recordingState == RecordingState.Recording) onEvent(RecordAudioEvent.RecordingPaused) else onEvent(
                                RecordAudioEvent.RecordingResumed
                            )
                        }
                ) {
                    StrokeCircle(
                        color = PrimaryDefault,
                        modifier = Modifier.align(
                            Center
                        )
                    )

                    Image(
                        painter = if (recordingState == RecordingState.Recording) painterResource(R.drawable.pause_audio) else painterResource(
                            R.drawable.play_audio
                        ),
                        contentDescription = if (recordingState == RecordingState.Recording) "일시 정지" else "재개",
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

            Spacer(Modifier.height(60.dp))
        }


        if (recordingState == RecordingState.Ready) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .clickable(isRipple = true) {
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

            Spacer(Modifier.height(60.dp))
        }

        if (recordingState == RecordingState.Completed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 60.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(PrimaryActive)
                    .clickable() {
                        navigateToPlayAudio()
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
                        "피드백 받기", style = SpeechMateTheme.typography.bodyMSB, color = Color.White
                    )
                }

            }

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .clickable() {
                        onEvent(RecordAudioEvent.RecordingCanceled)
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
                        painter = painterResource(R.drawable.michrophone),
                        contentDescription = "재녹음",
                        modifier = Modifier
                            .size(24.dp),
                        colorFilter = ColorFilter.tint(PrimaryActive)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        "재녹음", style = SpeechMateTheme.typography.bodyMM, color = PrimaryActive
                    )
                }

            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun RecordAudioScreenPreview() {
    RecordAudioScreen(
        navigateBack = {},
        navigateToPlayAudio = {},
        recordingState = RecordingState.Recording,
        onEvent = {},
        elapsedTime = "00 : 00.00"
    )
}