package com.speech.practice.graph.playaudio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.speech.common.ui.StrokeCircle
import com.speech.common.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.practice.graph.playaudio.PlayAudioViewModel.PlayAudioEvent
import com.speech.practice.graph.playaudio.PlayAudioViewModel.PlayingAudioState

@Composable
internal fun PlayAudioRoute(
    viewModel: PlayAudioViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val playingAudioState by viewModel.playingAudioState.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTimeText.collectAsStateWithLifecycle()

    PlayAudioScreen(
        playingAudioState = playingAudioState,
        currentTime = currentTime,
        totalTime = viewModel.totalTimeText,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun PlayAudioScreen(
    playingAudioState: PlayingAudioState,
    currentTime: String,
    totalTime : String,
    onEvent: (PlayAudioEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Text(currentTime, style = TextStyle(fontSize = 50.sp, fontWeight = FontWeight.Light))

        Spacer(Modifier.height(10.dp))

        Text("총 발표 시간 : $totalTime", style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Light))

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp)
        ) {
            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clickable() {
                        if(playingAudioState == PlayingAudioState.Stopped) onEvent(PlayAudioEvent.PlayAudioStarted)
                        if(playingAudioState == PlayingAudioState.Playing) onEvent(PlayAudioEvent.PlayAudioStopped)
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
                    painter = if (playingAudioState == PlayingAudioState.Playing) painterResource(R.drawable.pause_audio) else painterResource(
                        R.drawable.play_audio
                    ),
                    contentDescription = if (playingAudioState == PlayingAudioState.Playing) "일시 정지" else "재개",
                    modifier = Modifier
                        .size(28.dp)
                        .align(
                            Center
                        ),
                    colorFilter = ColorFilter.tint(DarkGray)
                )
            }

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(40.dp))

    }
}

@Preview
@Composable
private fun PlayAudioScreenPreview() {
    PlayAudioScreen(
        currentTime = "00 : 00 . 00",
        totalTime = "1분 43초",
        playingAudioState = PlayingAudioState.Stopped,
        onEvent = {}
    )
}