package com.speech.mypage.graph.mypage

import android.net.Uri
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.speech.Audience
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.speech.SpeechType
import com.speech.domain.model.speech.Venue

data class MyPageState(
    val speechFeeds: List<SpeechFeed> = listOf(
        SpeechFeed(
            id = 1,
            fileName = "1분기 실적 발표",
            date = "23.10.27",
            fileLength = 123456L,
            fileUrl = "",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.EXPERT,
                venue = Venue.CONFERENCE_ROOM,
            ),
        ),
        SpeechFeed(
            id = 2,
            fileName = "신입사원 온보딩",
            date = "23.10.27",
            fileLength = 234567L,
            fileUrl = "",
            speechFileType = SpeechFileType.AUDIO,
            speechConfig = SpeechConfig(
                speechType = SpeechType.ACADEMIC_PRESENTATION,
                audience = Audience.BEGINNER,
                venue = Venue.EVENT_HALL,
            ),
        ),
        SpeechFeed(
            id = 3,
            fileName = "개발자 컨퍼런스 발표",
            date = "23.10.27",
            fileLength = 89012L,
            fileUrl = "",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.INTERMEDIATE,
                venue = Venue.LECTURE_HALL,
            ),
        ),
        SpeechFeed(
            id = 4,
            fileName = "투자 유치 발표",
            date = "23.10.27",
            fileLength = 345678L,
            fileUrl = "",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.EXPERT,
                venue = Venue.CONFERENCE_ROOM,
            ),
        ),

        SpeechFeed(
            id = 5,
            fileName = "팀 회의 발표",
            date = "23.10.27",
            fileLength = 500000L,
            fileUrl = "",
            speechFileType = SpeechFileType.AUDIO,
            speechConfig = SpeechConfig(
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.INTERMEDIATE,
                venue = Venue.CONFERENCE_ROOM,
            ),
        ),

        ),
) : UiState

sealed class MyPageIntent : UiIntent {
    data object OnSettingClick : MyPageIntent()
    data class OnSpeechClick(
        val speechId: Int,
        val fileUrl: String,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : MyPageIntent()
}

sealed interface MyPageSideEffect : UiSideEffect {
    data object NavigateToSetting : MyPageSideEffect
    data class NavigateToFeedback(
        val speechId: Int,
        val fileUrl: String,
        val speechFileType: SpeechFileType,
        val speechConfig: SpeechConfig,
    ) : MyPageSideEffect
}
