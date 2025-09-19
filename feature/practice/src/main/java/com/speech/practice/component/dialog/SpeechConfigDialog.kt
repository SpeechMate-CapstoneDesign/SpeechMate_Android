package com.speech.practice.component.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.speech.designsystem.component.SMOutlineButton
import com.speech.designsystem.component.SMOutlinedTextField
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue

@Composable
fun SpeechConfigDialog(
    onDone: (SpeechConfig) -> Unit,
    onDismiss: () -> Unit,
) {
    var speechConfig by remember { mutableStateOf(SpeechConfig()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 30.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text("발표 파일 설정", style = SpeechMateTheme.typography.headingSB)

                Spacer(Modifier.height(20.dp))

                Text("발표 이름", style = SpeechMateTheme.typography.bodySM)

                Spacer(Modifier.height(8.dp))

                SMOutlinedTextField(
                    value = speechConfig.fileName,
                    onValueChange = { speechConfig = speechConfig.copy(fileName = it) },
                    hint = "발표 파일 이름"
                )

                Spacer(Modifier.height(20.dp))

                Text("발표 상황", style = SpeechMateTheme.typography.bodySM)

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpeechType.entries.forEach {
                        SMOutlineButton(
                            cornerRadius = 24,
                            label = it.label,
                            isSelected = speechConfig.speechType == it,
                            onClick = { speechConfig = speechConfig.copy(speechType = it) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("청중", style = SpeechMateTheme.typography.bodySM)

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Audience.entries.forEach {
                        SMOutlineButton(
                            cornerRadius = 24,
                            label = it.label,
                            isSelected = speechConfig.audience == it,
                            onClick = { speechConfig = speechConfig.copy(audience = it) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text("발표 장소", style = SpeechMateTheme.typography.bodySM)

                Spacer(Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Venue.entries.forEach {
                        SMOutlineButton(
                            cornerRadius = 24,
                            label = it.label,
                            isSelected = speechConfig.venue == it,
                            onClick = { speechConfig = speechConfig.copy(venue = it) }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        onDone(speechConfig)
                        onDismiss()
                    },
                    enabled = speechConfig.isValid,
                    colors = ButtonDefaults.buttonColors(
                        if (speechConfig.isValid) PrimaryActive else PrimaryDefault
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        "완료",
                        color = Color.White,
                        style = SpeechMateTheme.typography.bodyXMM
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun SpeechConfigDialogPreview() {
    SpeechConfigDialog(
        onDone = {},
        onDismiss = {}
    )
}
