package com.speech.mypage.graph.webview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speech.common_ui.ui.SMWebView

@Composable
internal fun WebViewRoute(
    url: String,
    viewModel: WebViewViewModel = hiltViewModel()
) {
    WebViewScreen(
        url = url,
    )
}

@Composable
private fun WebViewScreen(
    url: String,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SMWebView(
            url = url,
            modifier = Modifier
                .imePadding(),
        )
    }
}
