package com.speech.mypage.graph.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.common_ui.util.clickable
import com.speech.common_ui.util.rememberDebouncedOnClick
import com.speech.designsystem.R
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.mypage.graph.setting.SettingViewModel
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun MyPageRoute(
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int, SpeechFileType, SpeechConfig) -> Unit,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.NavigateToSetting -> navigateToSetting()
            is MyPageSideEffect.NavigateToFeedback -> navigateToFeedBack(sideEffect.speechId, sideEffect.speechFileType, sideEffect.speechConfig)
        }
    }

    MyPageScreen(
        onSettingClick = { viewModel.onIntent(MyPageIntent.OnSettingClick) },
        onSpeechClick = { speechId, speechFileType, speechConfig ->
            viewModel.onIntent(
                MyPageIntent.OnSpeechClick(
                    speechId,
                    speechFileType,
                    speechConfig,
                ),
            )
        },
    )
}

@Composable
private fun MyPageScreen(
    onSettingClick: () -> Unit,
    onSpeechClick: (Int, SpeechFileType, SpeechConfig) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 48.dp),
        ) {
            item {
                Text(
                    "나의 스피치",
                    style = SpeechMateTheme.typography.headingMB,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, end = 10.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.setting_ic),
                contentDescription = "설정",
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .clickable(
                        onClick = rememberDebouncedOnClick {
                            onSettingClick()
                        },
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun MyPageScreenPreview() {
    MyPageScreen(
        onSettingClick = {},
        onSpeechClick = { _, _, _ -> },
    )
}
