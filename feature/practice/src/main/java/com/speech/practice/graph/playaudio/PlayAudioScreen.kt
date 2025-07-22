package com.speech.practice.graph.playaudio

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.designsystem.theme.audioWaveForm
import com.speech.practice.graph.playaudio.PlayAudioViewModel.PlayAudioEvent
import com.speech.practice.graph.playaudio.PlayAudioViewModel.PlayingAudioState

@Composable
internal fun PlayAudioRoute(
    viewModel: PlayAudioViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val audioDuration by viewModel.audioDuration.collectAsStateWithLifecycle()
    val playingAudioState by viewModel.playingAudioState.collectAsStateWithLifecycle()
    val currentTimeText by viewModel.currentTimeText.collectAsStateWithLifecycle()
    val currentTime by viewModel.currentTime.collectAsStateWithLifecycle()
    val amplitudes by viewModel.amplitudes.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = PlayAudioLifecycleObserver(
            onPauseAudio = { viewModel.onEvent(PlayAudioEvent.PlayAudioPaused) },
            onStopAudio = { viewModel.stopPlayAudio() }
        )
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    PlayAudioScreen(
        playingAudioState = playingAudioState,
        currentTimeText = currentTimeText,
        durationText = viewModel.durationText,
        currentTime = currentTime,
        duration = audioDuration,
        amplitudes = amplitudes,
        onEvent = viewModel::onEvent,
        seekTo = viewModel::seekTo,
        seekForward = viewModel::seekForward,
        seekBackward = viewModel::seekBackward
    )
}

@Composable
private fun PlayAudioScreen(
    playingAudioState: PlayingAudioState,
    currentTimeText: String,
    durationText: String,
    currentTime: Long,
    duration: Long,
    amplitudes: List<Int>,
    onEvent: (PlayAudioEvent) -> Unit,
    seekTo: (Long) -> Unit,
    seekForward: () -> Unit,
    seekBackward: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.7f))

        Text(currentTimeText, style = TextStyle(fontSize = 40.sp, fontWeight = FontWeight.Light))

        Spacer(Modifier.height(10.dp))

        Text(
            "총 발표 시간 : $durationText",
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Light)
        )

        Spacer(Modifier.height(60.dp))

        AudioWaveFormBox(
            amplitudes = amplitudes,
            currentTime = currentTime,
            duration = duration,
            seekTo = seekTo
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(50.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.seekbackward),
                    contentDescription = if (playingAudioState == PlayingAudioState.Playing) "일시 정지" else "재개",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            seekBackward()
                        }
                )

                Spacer(Modifier.height(2.dp))

                Text("-3초", style = SpeechMateTheme.typography.bodySM)
            }


            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clickable() {
                        if (playingAudioState == PlayingAudioState.Paused || playingAudioState == PlayingAudioState.Ready) onEvent(
                            PlayAudioEvent.PlayAudioStarted
                        )
                        if (playingAudioState == PlayingAudioState.Playing) onEvent(PlayAudioEvent.PlayAudioPaused)
                    }
            ) {
                com.speech.common_ui.ui.StrokeCircle(
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.seekforward),
                    contentDescription = if (playingAudioState == PlayingAudioState.Playing) "일시 정지" else "재개",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            seekForward()
                        }
                )

                Spacer(Modifier.height(2.dp))

                Text("+3초", style = SpeechMateTheme.typography.bodySM)
            }

            Spacer(Modifier.width(50.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}


@SuppressLint("DefaultLocale", "ConfigurationScreenWidthHeight")
@Composable
private fun AudioWaveFormBox(
    amplitudes: List<Int>,
    currentTime: Long,
    duration: Long,
    seekTo: (Long) -> Unit
) {
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    // 화면 너비
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val screenWidthPx = with(density) { screenWidthDp.toPx() }

    // 최소 1분 이상 보장
    val seconds = (duration / 1000).toInt().coerceAtLeast(60)

    // 전체 wave 영역 너비
    val waveformWidthPx = screenWidthPx * seconds / 8
    val waveformWidthDp = with(density) { waveformWidthPx.toDp() }


    // 진행도 (0 ~ 1f)
    val progress = remember { mutableFloatStateOf(0f) }
    val isDragging = remember { mutableStateOf(false) }


    LaunchedEffect(currentTime, !isDragging.value) {
        if(!isDragging.value) progress.floatValue = currentTime.toFloat() / duration
    }

    // 기준선 위치
    val standardLinePosition = waveformWidthPx * progress.floatValue

    // 기준선 따라 자동 스크롤
    LaunchedEffect(standardLinePosition, !isDragging.value) {
        if(!isDragging.value) scrollState.scrollTo(
            (standardLinePosition - screenWidthPx / 2).coerceAtLeast(0f).toInt()
        )
    }

    // 화면 전체는 스크롤 가능
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        // 실제 콘텐츠 너비는 전체 waveform 길이
        Box(
            modifier = Modifier
                .width(waveformWidthDp + 100.dp) // 마지막 위치 보정
                .height(300.dp)
                .background(audioWaveForm)
                .padding(horizontal = 50.dp).pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { isDragging.value = true },
                    onHorizontalDrag = { change, dragAmount ->
                        val newProgress =
                            (progress.floatValue + dragAmount / waveformWidthPx).coerceIn(
                                0f,
                                1f
                            )
                        progress.floatValue = newProgress
                    },
                    onDragEnd = {
                        isDragging.value = false
                        val newTime = (progress.floatValue * duration).toLong()
                        seekTo(newTime)

                    }
                )
            },
        ) {
            // 눈금 및 시간 텍스트
            Canvas(modifier = Modifier.fillMaxSize()) {
                val tickHeight = 20.dp.toPx()
                val tickInterval = 500L
                val spacePerMs = waveformWidthPx / duration.toFloat()

                for (i in 0..duration step tickInterval) {
                    val x = i * spacePerMs
                    val timeInSec = i / 1000

                    // 눈금선
                    drawLine(
                        color = Color.Gray,
                        start = Offset(x, 0f),
                        end = Offset(x, tickHeight),
                        strokeWidth = 1f
                    )

                    // 눈금 텍스트는 2초 단위
                    if (i.toInt() % 2000 == 0) {
                        drawContext.canvas.nativeCanvas.drawText(
                            String.format("%02d:%02d", timeInSec / 60, timeInSec % 60),
                            x,
                            tickHeight - 30.dp.toPx(),
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 30f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                        )
                    }
                }
            }

            AudioWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Center),
                amplitudes = amplitudes,
                progress = progress.floatValue,
                onProgressChange = {},
                onProgressChangeFinished = {},
                waveformAlignment = WaveformAlignment.Center,
                amplitudeType = AmplitudeType.Min,
                spikeWidth = 2.dp,
                spikePadding = 2.dp,
                spikeRadius = 0.dp,
                waveformBrush = SolidColor(Color.LightGray),
                progressBrush = SolidColor(Color.LightGray),
            )

            // 기준선 (진행 위치)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = PrimaryActive,
                    start = Offset(standardLinePosition, 0f),
                    end = Offset(standardLinePosition, size.height),
                    strokeWidth = 4f
                )
            }
        }
    }
}


@Preview
@Composable
private fun PlayAudioScreenPreview() {
    PlayAudioScreen(
        currentTime = 0L,
        currentTimeText = "00 : 00 . 00",
        duration = 10000L,
        durationText = "1분 43초",
        playingAudioState = PlayingAudioState.Paused,
        amplitudes = listOf(
            10, 12, 15, 18, 22, 26, 20, 18, 15, 13,
            12, 14, 16, 18, 21, 25, 30, 27, 22, 18,
            14, 11, 10, 10, 11, 13, 15, 17, 19, 21,
            23, 20, 17, 14, 11, 10, 10, 10, 15, 20,
            25, 30, 28, 24, 20, 16, 13, 11, 10, 10,
            12, 14, 18, 22, 26, 29, 27, 23, 19, 15,
            12, 10, 10, 10, 13, 16, 19, 22, 24, 26,
            23, 20, 17, 14, 11, 10, 10, 10, 15, 18,
            21, 25, 28, 30, 27, 23, 18, 14, 11, 10
        ),
        onEvent = {},
        seekTo = {},
        seekForward = {},
        seekBackward = {}
    )
}