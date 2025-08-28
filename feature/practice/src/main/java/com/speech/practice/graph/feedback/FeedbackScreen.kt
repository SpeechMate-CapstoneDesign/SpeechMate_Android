package com.speech.practice.graph.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.domain.model.speech.FeedbackTab
import com.speech.practice.graph.recordaudio.RecordAudioViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun FeedbackRoute(
    navigateToBack: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FeedbackSideEffect.ShowSnackbar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }
            is FeedbackSideEffect.NavigateToBack -> navigateToBack()
        }
    }

    FeedbackScreen(
        state = state,
        onBackPressed = {
            viewModel.onIntent(FeedbackIntent.OnBackPressed)
        },
        onTabSelected = { tab ->
            viewModel.onIntent(FeedbackIntent.OnTabSelected(tab))
        },
        onStartPlaying = {
            viewModel.onIntent(FeedbackIntent.StartPlaying)
        },
        onPausePlaying = {
            viewModel.onIntent(FeedbackIntent.PausePlaying)
        },
        onResumePlaying = {
            viewModel.onIntent(FeedbackIntent.ResumePlaying)
        }
    )
}

@Composable
private fun FeedbackScreen(
    state: FeedbackState,
    onBackPressed: () -> Unit,
    onTabSelected: (FeedbackTab) -> Unit,
    onStartPlaying: () -> Unit,
    onPausePlaying: () -> Unit,
    onResumePlaying: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, top = 55.dp)
        ) {
            item {

            }
        }
    }
}