package com.speech.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.speech.designsystem.R
import org.w3c.dom.Text

val SpoqaBold = FontFamily(
    Font(
        resId = R.font.spoqa_han_sans_neo_bold,
        weight = FontWeight.Bold
    )
)

val PretendardMedium = FontFamily(
    Font(
        resId = R.font.pretendard_medium,
        weight = FontWeight.Medium
    )
)

@Immutable
data class SpeechMateTypography(
    val headingMB : TextStyle = TextStyle(
        fontFamily = SpoqaBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val bodyMM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
)