package com.speech.common_ui.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.SpeechMateTheme

@Composable
fun SMOutlineButton(
    cornerRadius: Int = 8,
    label : String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = if (isSelected) PrimaryActive else Color.Gray,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PrimaryActive else Color.Gray
        ), shape = RoundedCornerShape(cornerRadius.dp)
    ) {
        Text(label, style = SpeechMateTheme.typography.bodySM)
    }
}