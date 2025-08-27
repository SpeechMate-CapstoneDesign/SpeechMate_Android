package com.speech.common_ui.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R

@Composable
fun BackButton(
    color: Color = Color.Black,
    onBackPressed: () -> Unit,
) {
    IconButton(
        onClick = onBackPressed,
    ) {
        Icon(
            painter = painterResource(R.drawable.back_button),
            contentDescription = "뒤로 가기",
            modifier = Modifier.size(36.dp),
            tint = color
        )
    }
}
