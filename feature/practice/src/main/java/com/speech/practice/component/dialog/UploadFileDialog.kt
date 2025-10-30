package com.speech.practice.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.speech.designsystem.R
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.upload.UploadFileStatus

@Composable
fun UploadFileDialog(
    status: UploadFileStatus,
    onDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .background(SmTheme.colors.surface)
                .padding(horizontal = 24.dp, vertical = 70.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.uploading_file_description),
                        style = SmTheme.typography.bodyMM,
                        color = SmTheme.colors.textPrimary,
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "(${status.elapsedSeconds.inWholeSeconds}초)",
                        style = SmTheme.typography.bodyXSM,
                        color = SmTheme.colors.textSecondary,
                    )
                }

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { status.progress / 100 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SmTheme.colors.primaryDefault,
                    trackColor = SmTheme.colors.iconDefault,
                    strokeCap = StrokeCap.Square,
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "%.1f%%".format(status.progress),
                        style = SmTheme.typography.bodyXSM,
                        color = SmTheme.colors.textSecondary,
                    )

                    Text(
                        text = status.formattedBytes,
                        style = SmTheme.typography.bodyXSM,
                        color = SmTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun UploadFileDialogPreview() {
        UploadFileDialog(
            status = UploadFileStatus(
                currentBytes = 250000,
                totalBytes = 5000000,
            ),
            onDismiss = {},
        )
}
