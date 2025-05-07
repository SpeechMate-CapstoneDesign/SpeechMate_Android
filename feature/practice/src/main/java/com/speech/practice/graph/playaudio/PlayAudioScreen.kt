package com.speech.practice.graph.playaudio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.practice.graph.playaudio.PlayAudioViewModel.PlayAudioEvent

@Composable
internal fun PlayAudioRoute(
    viewModel: PlayAudioViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    PlayAudioScreen(
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun PlayAudioScreen(
    onEvent : (PlayAudioEvent) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {

        }
    }
}

@Preview
@Composable
private fun PlayAudioScreenPreview() {
    PlayAudioScreen(
        onEvent = {}
    )
}