package com.speech.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme

@Composable
fun SMOutlineButton(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 8,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SmTheme.colors.surface,
            contentColor = if (isSelected) SmTheme.colors.primaryDefault else SmTheme.colors.textSecondary,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) SmTheme.colors.primaryDefault else Color.Gray,
        ),
        shape = RoundedCornerShape(cornerRadius.dp),
        contentPadding = PaddingValues(start = 18.dp),
    ) {
        Text(label, style = SmTheme.typography.bodySM)

        Spacer(Modifier.weight(1f))
    }
}
