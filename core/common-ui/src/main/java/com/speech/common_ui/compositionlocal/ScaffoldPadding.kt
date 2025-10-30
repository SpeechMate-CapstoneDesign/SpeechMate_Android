package com.speech.common_ui.compositionlocal

import androidx.compose.runtime.staticCompositionLocalOf

val LocalShouldApplyScaffoldPadding = staticCompositionLocalOf { true }
val LocalSetShouldApplyScaffoldPadding = staticCompositionLocalOf<(Boolean) -> Unit> {
    error("No SetShouldApplyScaffoldPadding provided")
}
