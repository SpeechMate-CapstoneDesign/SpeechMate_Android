package com.speech.practice.graph.feedback.component

import android.content.pm.ActivityInfo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import com.speech.common_ui.ui.ScreenOrientationEffect
import com.speech.common_ui.ui.rememberSystemUiController
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.component.SimpleCircle
import com.speech.designsystem.theme.SmTheme
import com.speech.practice.graph.feedback.FeedbackState
import com.speech.practice.graph.feedback.PlayingState
import kotlinx.coroutines.delay
import kotlin.times

@Composable
internal fun FeedbackPlayer(
    state: FeedbackState,
    exoPlayer: ExoPlayer?,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onProgressChanged: (Long) -> Unit,
    onFullScreenClick: () -> Unit,
) {
    val isPlaying = state.playingState == PlayingState.Playing
    val systemUiController = rememberSystemUiController()
    var controlsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(controlsVisible, isPlaying) {
        if (controlsVisible && isPlaying) {
            delay(3000)
            controlsVisible = false
        }
    }

    DisposableEffect(state.isFullScreen) {
        if (state.isFullScreen) {
            systemUiController?.hideSystemBars()
        } else {
            systemUiController?.showSystemBars()
        }

        onDispose {
            systemUiController?.showSystemBars()
        }
    }

    if (state.isFullScreen) {
        ScreenOrientationEffect(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    } else {
        ScreenOrientationEffect(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                controlsVisible = !controlsVisible
            },
    ) {
        PlayerSurface(
            player = exoPlayer,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (state.isFullScreen) Modifier else Modifier.aspectRatio(16f / 10f),
                )
                .align(Alignment.Center),
        )

        when (state.playingState) {
            is PlayingState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SmTheme.colors.primaryDefault,
                )
            }

            is PlayingState.Error -> {
                Text(
                    stringResource(R.string.error_failed_to_load_media),
                    modifier = Modifier.align(Alignment.Center),
                    color = SmTheme.colors.white,
                    style = SmTheme.typography.bodySM,
                )
            }

            else -> {}
        }

        if (controlsVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlayerControlButton(
                    onClick = onSeekBackward,
                    iconRes = R.drawable.seek_backward_ic,
                    contentDescription = "10초 전",
                    diameter = 48.dp,
                    iconSize = 24.dp,
                )

                PlayerControlButton(
                    onClick = { if (isPlaying) onPausePlaying() else onStartPlaying() },
                    iconRes = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                    contentDescription = if (isPlaying) "일시정지" else "재생",
                    diameter = 64.dp,
                    iconSize = 32.dp,
                )

                PlayerControlButton(
                    onClick = onSeekForward,
                    iconRes = R.drawable.seek_forward_ic,
                    contentDescription = "10초 후",
                    diameter = 48.dp,
                    iconSize = 24.dp,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp)
                    .align(Alignment.BottomCenter),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.playerState.formattedCurrentPosition,
                        style = SmTheme.typography.bodyXSM,
                        color = SmTheme.colors.white,
                    )

                    Text(
                        text = " / ${state.playerState.formattedDuration}",
                        style = SmTheme.typography.bodyXSM,
                        color = SmTheme.colors.white,
                    )

                    Spacer(Modifier.weight(1f))

                    PlayerControlButton(
                        onClick = onFullScreenClick,
                        iconRes = R.drawable.full_screen_ic,
                        contentDescription = "전체 화면",
                        diameter = 32.dp,
                        iconSize = 24.dp,
                    )
                }

                PlayerProgressSlider(
                    duration = state.playerState.duration.inWholeMilliseconds,
                    progress = state.playerState.progress,
                    onProgressChanged = onProgressChanged,
                    onSeekTo = onSeekTo,
                )
            }
        }
    }
}


@Composable
private fun PlayerControlButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    diameter: Dp,
    iconSize: Dp,
) {
    Box(
        modifier = Modifier.clickable { onClick() },
    ) {
        SimpleCircle(
            diameter = diameter,
            color = SmTheme.colors.black.copy(0.4f),
            modifier = Modifier.align(Alignment.Center),
        )

        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = SmTheme.colors.white,
            modifier = Modifier
                .size(iconSize)
                .align(Alignment.Center),
        )
    }
}

@Composable
fun PlayerProgressSlider(
    modifier: Modifier = Modifier,
    duration: Long,
    progress: Float,
    onProgressChanged: (Long) -> Unit,
    onSeekTo: (Long) -> Unit,
    trackHeight: Dp = 6.dp,
    thumbRadius: Dp = 6.dp,
    activeColor: Color = SmTheme.colors.primaryDefault,
    inactiveColor: Color = SmTheme.colors.iconDefault,
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var sliderWidth by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(sliderValue) {
        if (isDragging) {
            val newPosition = (sliderValue * duration).toLong()
            onProgressChanged(newPosition)
        }
    }

    LaunchedEffect(progress) {
        if (!isDragging) {
            sliderValue = progress
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadius * 4)
            .onSizeChanged { sliderWidth = it.width.toFloat() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        val newPosition = (sliderValue * duration).toLong()
                        onSeekTo(newPosition)
                    },
                    onDragCancel = {
                        isDragging = false
                        sliderValue = progress
                    },
                    onDrag = { change, _ ->
                        if (sliderWidth > 0) {
                            val newValue = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                            sliderValue = newValue
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (sliderWidth > 0) {
                        val newValue = (offset.x / sliderWidth).coerceIn(0f, 1f)
                        sliderValue = newValue
                        val newPosition = (sliderValue * duration).toLong()
                        onSeekTo(newPosition)
                    }
                }
            },
    ) {
        val centerY = size.height / 2
        val trackHeightPx = trackHeight.toPx()
        val thumbRadiusPx = thumbRadius.toPx()
        val thumbX = size.width * sliderValue

        // Inactive track
        drawRoundRect(
            color = inactiveColor,
            topLeft = Offset(0f, centerY - trackHeightPx / 2),
            size = Size(size.width, trackHeightPx),
            cornerRadius = CornerRadius(trackHeightPx / 2, trackHeightPx / 2),
        )

        // Active track
        drawRoundRect(
            color = activeColor,
            topLeft = Offset(0f, centerY - trackHeightPx / 2),
            size = Size(thumbX, trackHeightPx),
            cornerRadius = CornerRadius(trackHeightPx / 2, trackHeightPx / 2),
        )

        // Thumb
        drawCircle(
            color = activeColor,
            radius = thumbRadiusPx * if (isDragging) 1.4f else 1f,
            center = Offset(thumbX, centerY),
        )
    }
}
