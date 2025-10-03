package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.SmTheme
import com.speech.practice.graph.feedback.FeedbackState
import com.speech.practice.graph.feedback.PlayingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaControls(
    state: FeedbackState,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onChangePlaybackSpeed: (Float) -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(state.playerState.progress) {
        if (!isDragging) {
            sliderValue = state.playerState.progress
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isPlaying = state.playingState == PlayingState.Playing

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clickable { if (isPlaying) onPausePlaying() else onStartPlaying() },
            ) {
                Icon(
                    painter = if (isPlaying) {
                        painterResource(R.drawable.ic_pause)
                    } else {
                        painterResource(R.drawable.ic_play)
                    },
                    contentDescription = if (isPlaying) "일시정지" else "재생",
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(Modifier.width(12.dp))

            Slider(
                value = sliderValue,
                onValueChange = {
                    isDragging = true
                    sliderValue = it
                },
                onValueChangeFinished = {
                    isDragging = false
                    val newPosition = (sliderValue * state.playerState.duration.inWholeMilliseconds).toLong()
                    onSeekTo(newPosition)
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = SmTheme.colors.primaryDefault,
                    inactiveTrackColor = SmTheme.colors.iconDefault,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .shadow(elevation = 1.dp, shape = CircleShape)
                            .background(color = SmTheme.colors.primaryDefault, shape = CircleShape),
                    )
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = SmTheme.colors.primaryDefault,
                            inactiveTrackColor = SmTheme.colors.iconDefault,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                        ),
                        thumbTrackGapSize = 0.dp,
                        modifier = Modifier.height(8.dp),
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(6.dp))

        Row {
            Text(
                text = state.playerState.formattedCurrentPosition,
                style = SmTheme.typography.bodySM,
            )

            Text(
                text = " / ${state.playerState.formattedDuration}",
                style = SmTheme.typography.bodySM,
            )
        }
    }
}
