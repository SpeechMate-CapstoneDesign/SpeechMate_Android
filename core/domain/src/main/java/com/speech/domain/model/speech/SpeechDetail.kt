package com.speech.domain.model.speech

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SpeechDetail(
    val id: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val fileUrl: String = "",
    val speechFileType: SpeechFileType = SpeechFileType.AUDIO,
    val speechConfig: SpeechConfig = SpeechConfig(),
    val script: String = "",
    val scriptAnalysis: ScriptAnalysis = ScriptAnalysis(),
    val verbalAnalysis: VerbalAnalysis = VerbalAnalysis(),
    val nonVerbalAnalysis: NonVerbalAnalysis = NonVerbalAnalysis(),
) {
    val fornattedTime: String =
        createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
}

data class ScriptAnalysis(
    val summary: String = "",
    val keywords: String = "",
    val improvementPoints: String = "",
    val logicalCoherenceScore: Int = 0,
    val feedback: String = "",
    val scoreExplanation: String = "",
    val expectedQuestions: String = "",
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)
data class VerbalAnalysis(
    val isLoading: Boolean = true,
    val score: Int = 0,
)

data class NonVerbalAnalysis(
    val isLoading: Boolean = true,
    val score: Int = 0,
)
