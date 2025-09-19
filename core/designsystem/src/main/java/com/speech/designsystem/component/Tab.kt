package com.speech.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.Background
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.SpeechMateTheme

@Composable
fun SpeechMateTab(
    label: String,
    isSelected: Boolean,
    onTabSelected: () -> Unit,
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Color.LightGray,
                shape = RoundedCornerShape(8.dp),
            )
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) PrimaryActive else Background)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable {
                onTabSelected()
            },
    ) {
        Text(
            label,
            style = SpeechMateTheme.typography.bodyXMM,
            color = if (isSelected) Color.White else Color.LightGray,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Composable
fun SpeechMateTabPreview() {
    SpeechMateTab(
        label = "대본",
        isSelected = true,
        onTabSelected = {},
    )
}
