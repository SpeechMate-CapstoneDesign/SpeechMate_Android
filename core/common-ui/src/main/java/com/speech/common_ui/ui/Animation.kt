package com.speech.common_ui.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SpeechMateBottomBarAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contents: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    visible = visible,
    content = contents,
    modifier = modifier,
)