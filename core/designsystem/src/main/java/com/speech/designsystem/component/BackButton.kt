package com.speech.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.theme.SmTheme

@Composable
fun BackButton(
    color: Color = SmTheme.colors.content,
    onBackPressed: () -> Unit,
) {
    IconButton(
        onClick = onBackPressed,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "뒤로 가기",
            modifier = Modifier.size(20.dp),
            tint = color
        )
    }
}
