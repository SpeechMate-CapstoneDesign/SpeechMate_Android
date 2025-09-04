package com.speech.common_ui.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.LightGray

@Composable
fun SectionDivider(
    modifier : Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = LightGray,
    )
}
