package com.speech.mypage.graph.mypage

import android.net.Uri
import androidx.paging.PagingData
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class MyPageState(
    val speechFeeds: Flow<PagingData<SpeechFeed>> = emptyFlow(),
    val showDropDownMenu: Boolean = false,
) : UiState

sealed class MyPageIntent : UiIntent {
    data object OnSettingClick : MyPageIntent()
    data class OnSpeechClick(
        val speechId: Int,
        val fileUrl: String,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : MyPageIntent()

    data class OnDeleteClick(val speechId: Int) : MyPageIntent()
}

sealed interface MyPageSideEffect : UiSideEffect {
    data class ShowSnackbar(val message: String) : MyPageSideEffect
    data object NavigateToSetting : MyPageSideEffect
    data class NavigateToFeedback(
        val speechId: Int,
        val fileUrl: String,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : MyPageSideEffect
}
