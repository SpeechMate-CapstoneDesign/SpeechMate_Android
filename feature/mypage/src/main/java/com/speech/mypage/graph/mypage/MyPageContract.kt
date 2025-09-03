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
            id = 130,
            date = "23.10.27",
            fileLength = 123456L,
            fileUrl = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/ab7cd0fd-4431-4388-9591-8ab172797f99.wav",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                fileName = "1분기 실적 발표",
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.EXPERT,
                venue = Venue.CONFERENCE_ROOM,
            ),
        ),
        SpeechFeed(
            id = 94,
            date = "23.10.27",
            fileLength = 234567L,
            fileUrl = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/7c41daf5-9a1d-489d-a875-ff6a316256ff.mp4",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                fileName = "신입사원 온보딩",
                speechType = SpeechType.ACADEMIC_PRESENTATION,
                audience = Audience.BEGINNER,
                venue = Venue.EVENT_HALL,
            ),
        ),
        SpeechFeed(
            id = 93,
            date = "23.10.27",
            fileLength = 89012L,
            fileUrl = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/081d6550-e025-4001-95a9-746bc4617904.mp4",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                fileName = "개발자 컨퍼런스 발표",
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.INTERMEDIATE,
                venue = Venue.LECTURE_HALL,
            ),
        ),
        SpeechFeed(
            id = 117,
            date = "23.10.27",
            fileLength = 345678L,
            fileUrl = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/22c5b5c7-950d-4857-96ca-98a1f6a1437e.wav",
            speechFileType = SpeechFileType.VIDEO,
            speechConfig = SpeechConfig(
                fileName = "투자 유치 발표",
                speechType = SpeechType.BUSINESS_PRESENTATION,
                audience = Audience.EXPERT,
                venue = Venue.CONFERENCE_ROOM,
            ),
        ),

        SpeechFeed(
            id = 115,
            date = "23.10.27",
            fileLength = 500000L,
            fileUrl = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/63e2f068-29cf-4ed8-bd68-a0f19f34ae4d.wav",
            speechFileType = SpeechFileType.AUDIO,
            speechConfig = SpeechConfig(
                fileName = "팀 회의 발표",
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
