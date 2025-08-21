package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class GetTextAnalysisResponse(
    val id: Int,
    val sttContent: String,
    val fileUrl: String,
    val analysisResult: AnalysisResult
)

@Serializable
data class AnalysisResult(
    val summary: String,
    val keywords: String,
    val improvementPoints: String,
    val logicalCoherenceScore: Int,
    val scoreExplanation: String,
    val expectedQuestions: String,
    val feedback: String
)
