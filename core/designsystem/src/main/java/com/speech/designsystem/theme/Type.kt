package com.speech.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.speech.designsystem.R


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

val PretendardSemiBold = FontFamily(
    Font(
        resId = R.font.pretendard_semi_bold,
        weight = FontWeight.SemiBold
    )
)

@Immutable
data class SpeechMateTypography(
    val headingXLB: TextStyle = TextStyle(
        fontFamily = SpoqaBold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    val headingMB: TextStyle = TextStyle(
        fontFamily = SpoqaBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val bodyXLM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 50.sp,
        lineHeight = 58.sp
    ),
    val bodyMM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyMSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
)