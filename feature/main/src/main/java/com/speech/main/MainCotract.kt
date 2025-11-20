package com.speech.main

import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.domain.model.speech.FeedbackTab
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType

sealed class MainIntent : UiIntent {
    data class OnNotificationClick(val speechId: Int, val type : String) : MainIntent()
}


sealed interface MainSideEffect : UiSideEffect {
    data class ShowSnackbar(val message : String, val action : () -> Unit) : MainSideEffect
    data class NavigateToFeedback(val speechId : Int, val tab: FeedbackTab) : MainSideEffect
}
