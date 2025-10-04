package com.speech.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.speech.designsystem.R


val SpoqaBold = FontFamily(Font(R.font.spoqa_han_sans_neo_bold, FontWeight.Bold))
val PretendardMedium = FontFamily(Font(R.font.pretendard_medium, FontWeight.Medium))
val PretendardSemiBold = FontFamily(Font(R.font.pretendard_semi_bold, FontWeight.SemiBold))

private val defaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun textStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) = TextStyle(
    fontFamily = fontFamily,
    fontSize = fontSize,
    lineHeight = lineHeight,
    lineHeightStyle = defaultLineHeightStyle,
)

@Immutable
data class SpeechMateTypography(
    // haeding
    val headingXLB: TextStyle = textStyle(SpoqaBold, 36.sp, 44.sp),
    val headingMB: TextStyle = textStyle(SpoqaBold, 24.sp, 32.sp),
    val headingSB: TextStyle = textStyle(SpoqaBold, 18.sp, 22.sp),
    val headingXSB : TextStyle = textStyle(SpoqaBold, 16.sp, 22.sp),

    // body
    val bodyXLM: TextStyle = textStyle(PretendardMedium, 50.sp, 58.sp),
    val bodyMM: TextStyle = textStyle(PretendardMedium, 20.sp, 24.sp),
    val bodyXMM: TextStyle = textStyle(PretendardMedium, 16.sp, 22.sp),
    val bodySM: TextStyle = textStyle(PretendardMedium, 14.sp, 18.sp),
    val bodyXSM: TextStyle = textStyle(PretendardMedium, 12.sp, 16.sp),
    val bodyMSB: TextStyle = textStyle(PretendardSemiBold, 20.sp, 24.sp),
    val bodyXMSB: TextStyle = textStyle(PretendardSemiBold, 16.sp, 18.sp),
)
