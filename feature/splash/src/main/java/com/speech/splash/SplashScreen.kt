package com.speech.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.theme.SmTheme

@Composable
internal fun SplashRoute() {
    SplashScreen()
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SmTheme.colors.primaryDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.9f))

        Image(
            painter = painterResource(R.drawable.ic_speechmate),
            contentDescription = "앱 아이콘",
            modifier = Modifier.fillMaxSize(0.5f),
        )

        Spacer(Modifier.weight(1f))
    }
}


@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
