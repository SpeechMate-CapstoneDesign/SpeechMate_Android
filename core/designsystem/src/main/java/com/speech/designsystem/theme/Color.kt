package com.speech.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import com.google.firebase.annotations.concurrent.Background

val PrimaryDefault = Color(0xFF42A5F5)
val PrimaryLight = Color(0xFFBBDEFB)
private val PrimaryGradientStart = Color(0xFF60A5FA)
private val PrimaryGradientEnd = Color(0xFFBFDBFE)
private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000)
private val Gray900 = Color(0xFF111827)
private val Gray800 = Color(0xFF1F2937)
private val Gray700 = Color(0xFF374151)
private val Gray600 = Color(0xFF4B5563)
private val Gray500 = Color(0xFF6B7280)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray50 = Color(0xFFF9FAFB)
private val Red = Color(0xFFFF0000)
private val Green = Color(0xFF4CAF50)
private val Purple = Color(0xFF673AB7)

val lightColorScheme = SpeechMateColors(
    primaryDefault = PrimaryDefault,
    primaryLight = PrimaryLight,
    background = White,
    surface = White,
    border = LightGray,
    textPrimary = Black,
    textSecondary = Gray400,
    textHint = Gray400,
    bottomIconDefault = Gray200,
    iconDefault = Gray300,
)

val darkColorScheme = SpeechMateColors(
    primaryDefault = PrimaryDefault,
    primaryLight = PrimaryLight,
    background = Gray900,
    surface = Gray800,
    border = Gray700,
    textPrimary = White,
    textSecondary = Gray400,
    textHint = Gray400,
    bottomIconDefault = Gray500,
    iconDefault = Gray300,
)

@Immutable
data class SpeechMateColors(
    val primaryDefault: Color = PrimaryDefault,
    val primaryLight: Color = PrimaryLight,
    val primaryGradientStart: Color = PrimaryGradientStart,
    val primaryGradientEnd: Color = PrimaryGradientEnd,
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textHint: Color,
    val bottomIconDefault: Color,
    val iconDefault: Color,
    val red: Color = Red,
    val green : Color = Green,
    val purple: Color = Purple,
    val black : Color = Black,
    val white: Color = White,
)

