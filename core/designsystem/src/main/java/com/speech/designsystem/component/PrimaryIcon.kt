package com.speech.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speech.designsystem.theme.SmTheme

@Composable
fun PrimaryIcon(
    modifier: Modifier = Modifier,
    contentPadding : Int = 10,
    shape: Shape = RoundedCornerShape(8.dp),
    @DrawableRes icon: Int,
) {
    val primaryGradient = Brush.verticalGradient(
        colors = listOf(SmTheme.colors.primaryGradientStart, SmTheme.colors.primaryGradientEnd),
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = primaryGradient)
            .padding(contentPadding.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(SmTheme.colors.white),
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview
@Composable
private fun PrimaryIconPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        PrimaryIcon(icon = com.speech.designsystem.R.drawable.ic_record_audio)
    }
}
