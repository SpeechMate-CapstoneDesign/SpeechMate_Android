package com.speech.network.model.speech

import com.speech.domain.model.speech.ScriptAnalysis
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class ScriptAnalysisResponse(
    val summary: String,
    val keywords: String,
    val improvementPoints: List<String>,
    val expectedQuestions: List<String>,
    val feedback: String,
) {
    fun toDomain(): ScriptAnalysis {
        return ScriptAnalysis(
            summary = summary,
            keywords = keywords,
            improvementPoints = improvementPoints,
            feedback = feedback,
            expectedQuestions = expectedQuestions,
        )
    }
}
