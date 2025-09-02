package com.speech.common_ui.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme

@Composable
fun UploadFileDialog(
    onDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(start = 50.dp, end = 50.dp, top = 80.dp, bottom = 80.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("발표 파일 업로드 중입니다", style = SpeechMateTheme.typography.bodyMM)

                Spacer(Modifier.height(20.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = PrimaryDefault,
                )
            }
        }
    }
}

@Preview
@Composable
fun UploadFileDialogPreview() {
        UploadFileDialog(onDismiss = {})
}
