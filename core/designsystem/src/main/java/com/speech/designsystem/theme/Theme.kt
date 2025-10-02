package com.speech.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

private val LocalColors = compositionLocalOf {
    lightColorScheme
}

private val LocalTypography = staticCompositionLocalOf {
    SpeechMateTypography()
}

@Composable
fun SpeechMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }

    CompositionLocalProvider(
        LocalColors provides colors,
        content = content,
    )
}

object SmTheme {
    val colors: SpeechMateColors
        @Composable
        get() = LocalColors.current

    val typography: SpeechMateTypography
        @Composable
        get() = LocalTypography.current


}
