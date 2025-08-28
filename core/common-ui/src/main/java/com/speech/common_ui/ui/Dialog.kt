package com.speech.common_ui.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.theme.DarkGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme

@Composable
fun CheckCancelDialog(
    onCheck: () -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
    dialogText: String,
    checkText: String? = null,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            if (title != null) {
                Text(text = title, style = SpeechMateTheme.typography.bodyMSB)
            }
        },
        text = { Text(dialogText, style = SpeechMateTheme.typography.bodySM) },
        shape = RoundedCornerShape(8.dp),
        containerColor = White,
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onCheck()
            }) {
                Text(
                    if (checkText == null) stringResource(R.string.dialog_confirm) else checkText,
                    color = PrimaryActive,
                    style = SpeechMateTheme.typography.bodySM,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    stringResource(R.string.dialog_cancel),
                    color = DarkGray,
                    style = SpeechMateTheme.typography.bodySM,
                )
            }
        },
    )
}

@Preview
@Composable
fun CheckCancelDialogPreview() {
    SpeechMateTheme {
        CheckCancelDialog(
            onCheck = {},
            onDismiss = {},
            title = "제목",
            dialogText = "정말로 ~~ 하시겠습니까?"
        )
    }
}