package com.speech.domain.model.speech

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SpeechDetail(
    val id: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val fileUrl: String = "https://speechmate-s3.s3.ap-northeast-2.amazonaws.com/user/4/speech/7c41daf5-9a1d-489d-a875-ff6a316256ff.mp4",
    val speechFileType: SpeechFileType = SpeechFileType.AUDIO,
    val speechConfig: SpeechConfig = SpeechConfig(),
    val script: String = "",
    val scriptAnalysis: ScriptAnalysis? = null,
    val verbalAnalysis: VerbalAnalysis? = null,
    val nonVerbalAnalysis: NonVerbalAnalysis? = null,
) {
    val fornattedTime: String =
        createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
}

data class ScriptAnalysis(
    val summary: String,
    val keywords: String,
    val improvementPoints: String,
    val logicalCoherenceScore: Int,
    val feedback: String,
    val scoreExplanation: String,
    val expectedQuestions: String,
    val isError: Boolean = false,
)

data class VerbalAnalysis(
    val score: Int = 0,
)

data class NonVerbalAnalysis(
    val score: Int = 0,
)
