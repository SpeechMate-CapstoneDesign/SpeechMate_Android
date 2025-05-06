package com.speech.auth.graph

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.auth.R
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.auth.graph.LoginViewModel.LoginEvent
import com.speech.designsystem.theme.PrimaryLighter

@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToPractice: () -> Unit,
) {
    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> navigateToPractice()
                is LoginEvent.LoginFailure -> {}
            }
        }
    }

    LoginScreen()
}

@Composable
private fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryLighter)
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Text("SpeechMate", style = SpeechMateTheme.typography.headingXLB)

        Spacer(Modifier.height(30.dp))

        Image(
            painter = painterResource(com.speech.designsystem.R.drawable.kakao_login),
            contentDescription = "카카오로 로그인하기"
        )

        Spacer(Modifier.weight(1f))
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen()
}