package com.speech.network.model.speech

import com.speech.domain.model.speech.ScriptAnalysis
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class ScriptAnalysisResponse(
    val summary: String,
    val keywords: String,
    val improvementPoints: String,
    val logicalCoherenceScore: Int,
    val scoreExplanation: String,
    val expectedQuestions: String,
    val feedback: String
) {
    fun toDomain(): ScriptAnalysis {
        return ScriptAnalysis(
            summary = summary,
            keywords = keywords,
            improvementPoints = improvementPoints,
            logicalCoherenceScore = logicalCoherenceScore,
            feedback = feedback,
            scoreExplanation = scoreExplanation,
            expectedQuestions = expectedQuestions
        )
    }
}
