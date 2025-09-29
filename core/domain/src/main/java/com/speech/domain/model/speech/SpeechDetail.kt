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
    val nonVerbalAnalysis: String = "",
) {
    val formattedDate: String =
        createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"))
}

data class ScriptAnalysis(
    val summary: String = "",
    val keywords: String = "",
    val improvementPoints: List<String> = emptyList(),
    val feedback: String = "",
    val expectedQuestions: List<String> = emptyList(),
)


