package com.speech.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.google.firebase.annotations.concurrent.Background

private val PrimaryDefault = Color(0xFFBBDEFB)
private val PrimaryActive = Color(0xFF42A5F5)
private val White = Color(0xFFFFFFFF)

private val LightGray = Color(0xFFD3D3D3)
private val CloudGray = Color(0xFFCCCCCC)
private val DarkGray = Color(0xFF757575)
private val Purple = Color(0xFFCE93D8)
private val Green = Color(0xFFA5D6A7)

@Immutable
data class SpeechMateColors(
    val primaryDefault: Color = PrimaryDefault,
    val primaryActive: Color = PrimaryActive,
    val background: Color = White,
    val lightGray: Color = LightGray,
    val cloudGray: Color = CloudGray,
    val darkGray: Color = DarkGray,
    val purple: Color = Purple,
    val green : Color = Green,
)

