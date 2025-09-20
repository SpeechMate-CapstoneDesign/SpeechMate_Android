package com.speech.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.PrimaryDefault



@Composable
fun StrokeRoundRectangle(
    modifier: Modifier = Modifier,
    color: Color = PrimaryDefault,
    height: Dp = 50.dp,
    strokeWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp
) {
    Canvas(modifier = modifier.height(height).fillMaxWidth().padding(horizontal = 60.dp)) {
        val strokePx = strokeWidth.toPx()
        val radiusPx = cornerRadius.toPx()

        drawRoundRect(
            color = color,
            topLeft = Offset(strokePx / 2, strokePx / 2),
            size = Size(size.width - strokePx, size.height - strokePx),
            cornerRadius = CornerRadius(radiusPx, radiusPx),
            style = Stroke(width = strokePx)
        )
    }
}
