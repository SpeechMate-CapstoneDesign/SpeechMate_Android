package com.speech.network.model.speech

import com.speech.domain.model.speech.Script
import com.speech.domain.model.speech.Sentence
import kotlinx.serialization.Serializable
import okhttp3.internal.concurrent.formatDuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toDuration

@Serializable
data class ScriptResponse(
    val sentences: List<SentenceResponse>,
) {
    fun toDomain(): Script = Script(sentences = sentences.map { Sentence(it.startTime.milliseconds, it.sentence) })
}

@Serializable
data class SentenceResponse(
    val startTime: Long,
    val sentence: String,
)
