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
    val headingSB: TextStyle = TextStyle(
        fontFamily = SpoqaBold,
        fontSize = 18.sp,
        lineHeight = 22.sp
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
    val bodyXMM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    val bodySM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    val bodyXSM: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    val bodyMSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    val bodyXMSB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 16.sp,
        lineHeight = 18.sp
    ),
)
