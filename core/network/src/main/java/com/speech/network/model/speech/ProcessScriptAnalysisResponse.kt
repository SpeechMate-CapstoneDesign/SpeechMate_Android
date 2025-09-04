package com.speech.network.model.speech

import com.speech.domain.model.speech.ScriptAnalysis
import kotlinx.serialization.Serializable

@Serializable
data class ProcessScriptAnalysisResponse(
    val analysisResult: ScriptAnalysisResponse
) {
    fun toDomain() : ScriptAnalysis = analysisResult.toDomain()

}

