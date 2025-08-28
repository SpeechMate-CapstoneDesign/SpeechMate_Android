package com.speech.domain.model.speech

import java.time.LocalDateTime

data class SpeechDetail(
    val id: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val fileUrl: String = "",
    val speechFileType: SpeechFileType = SpeechFileType.AUDIO,
    val speechConfig: SpeechConfig = SpeechConfig(),
    val script: String = "",
    val scriptAnalysis: ScriptAnalysis? = null,
    val verbalAnalysis: VerbalAnalysis? = null,
    val nonVerbalAnalysis: NonVerbalAnalysis? = null,
)

data class ScriptAnalysis(
    val summary: String,
    val keywords: String,
    val improvementPoints: String,
    val logicalCoherenceScore: Int,
    val feedback: String,
    val scoreExplanation: String,
    val expectedQuestions: String
)

data class VerbalAnalysis(
    val score : Int = 0,
)


data class NonVerbalAnalysis(
    val score : Int = 0,
)