package com.example.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import kotlinx.coroutines.delay

@Composable
fun SpeechMateSnackBar(
    snackBarData: SnackbarData
) {
    val message = snackBarData.visuals.message

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 36.dp)
            .wrapContentSize()
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryActive)
            .padding(start = 20.dp, end = 80.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Text(
            text = message,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            color = Color.White,
        )
    }
}

@Composable
fun SpeechMateSnackBarHost(
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) },
) {
    val currentSnackbarData = hostState.currentSnackbarData

    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            delay(2000L)
            currentSnackbarData.dismiss()
        }
    }

    Crossfade(
        targetState = hostState.currentSnackbarData,
        modifier = modifier,
        label = "",
        content = { current -> if (current != null) snackbar(current) },
    )
}

@Preview
@Composable
private fun SpeechMateSnackBarPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeechMateSnackBar(
            snackBarData = object : SnackbarData {
                override val visuals: SnackbarVisuals = object : SnackbarVisuals {
                    override val actionLabel = null
                    override val duration = SnackbarDuration.Short
                    override val message = "로그인에 실패했습니다."
                    override val withDismissAction = false
                }

                override fun dismiss() {}
                override fun performAction() {}
            }
        )
    }
}
