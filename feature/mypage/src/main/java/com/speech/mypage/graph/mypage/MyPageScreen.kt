package com.speech.mypage.graph.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.mypage.graph.setting.SettingViewModel
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun MyPageRoute(
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int) -> Unit,
    viewModel: MyPageViewModel = hiltViewModel()
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToSetting -> navigateToSetting()
            is MyPageSideEffect.NavigateToFeedback -> navigateToFeedBack(sideEffect.speechId)
        }
    }
    
    MyPageScreen(
        onSettingClick = { viewModel.onIntent(MyPageIntent.OnSettingClick) },
        onSpeechClick = { speechId -> viewModel.onIntent(MyPageIntent.OnSpeechClick(speechId)) }
    )
}

@Composable
private fun MyPageScreen(
    onSettingClick: () -> Unit,
    onSpeechClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 10.dp, top = 10.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.setting_ic),
                        contentDescription = "설정",
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.TopEnd)
                            .clickable {
                                onSettingClick()
                            }
                    )
                }

            }
        }
    }
}

@Preview
@Composable
private fun MyPageScreenPreview() {
    MyPageScreen(
        onSettingClick = {},
        onSpeechClick = {}
    )
}