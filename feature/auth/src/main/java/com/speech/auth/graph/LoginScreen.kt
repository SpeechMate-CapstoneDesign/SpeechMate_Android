package com.speech.auth.graph

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToOnboarding : () -> Unit,
    navigateToPractice : () -> Unit,
) {
    LoginScreen()
}

@Composable
private fun LoginScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

    }
}

@Preview
@Composable
private fun LoginScreenPreview() {

}