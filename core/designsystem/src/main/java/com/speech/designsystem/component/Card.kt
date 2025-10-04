package com.speech.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.SmTheme

@Composable
fun SMCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier,
        colors = CardColors(
            containerColor = SmTheme.colors.surface,
            contentColor = Color.Transparent,
            disabledContainerColor = SmTheme.colors.surface,
            disabledContentColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SmTheme.colors.border),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        content()
    }
}
