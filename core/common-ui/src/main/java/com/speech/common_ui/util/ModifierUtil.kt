package com.speech.common_ui.util

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

@Composable
fun Modifier.clickable(
    enabled: Boolean = true,
    isRipple: Boolean = false,
    onClick: () -> Unit,
): Modifier = composed {
    this.clickable(
        indication = if (isRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun Modifier.combinedClickable(
    enabled: Boolean = true,
    isRipple: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
): Modifier = composed {
    this.combinedClickable(
        indication = if (isRipple) LocalIndication.current else null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}
