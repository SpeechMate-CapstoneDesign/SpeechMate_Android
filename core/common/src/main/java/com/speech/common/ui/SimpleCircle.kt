package com.speech.common.ui


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault

@Composable
fun SimpleCircle(
    modifier: Modifier = Modifier,
    color: Color = PrimaryActive,
    diameter: Dp = 115.dp
) {
    Canvas(modifier = modifier.size(diameter)) {
        val radius = size.minDimension / 2f
        drawCircle(
            color = color,
            radius = radius,
            center = Offset(x = size.width / 2f, y = size.height / 2f)
        )
    }
}

@Composable
fun StrokeCircle(
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    diameter: Dp = 50.dp,
    strokeWidth: Dp = 2.dp
) {
    Canvas(modifier = modifier.size(diameter)) {
        val strokePx = strokeWidth.toPx()

        val radius = size.minDimension / 2f - strokePx / 2f

        drawCircle(
            color = color,
            radius = radius,
            style = Stroke(width = strokePx)
        )
    }
}