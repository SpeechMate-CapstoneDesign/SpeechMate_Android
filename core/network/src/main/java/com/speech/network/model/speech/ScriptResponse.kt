package com.speech.network.model.speech

import com.speech.domain.model.speech.AnalysisStatus
import com.speech.domain.model.speech.Script
import com.speech.domain.model.speech.Sentence
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class ScriptResponse(
    val sttStatus: String = "COMPLETED",
    val sentences: List<SentenceResponse>? = null, // COMPLETED일 때만 온다
) {
    fun toDomain(): Script = Script(
        status = AnalysisStatus.fromString(sttStatus),
        sentences = sentences.orEmpty().map { Sentence(it.startTime.milliseconds, it.sentence) },
    )
}

@Serializable
data class SentenceResponse(
    val startTime: Long,
    val sentence: String,
)
