package com.speech.mypage.graph.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speech.common_ui.ui.SMWebView

@Composable
internal fun WebViewRoute(
    innerPadding: PaddingValues,
    url: String,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    WebViewScreen(
        innerPadding = innerPadding,
        url = url,
    )
}

@Composable
private fun WebViewScreen(
    innerPadding: PaddingValues,
    url: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        SMWebView(
            url = url,
            modifier = Modifier
                .imePadding(),
        )
    }
}
