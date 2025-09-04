package com.speech.network.model.speech

import kotlinx.serialization.Serializable

@Serializable
data class ScriptResponse(
    val content: String,
) {
    fun toDomain(): String = content
}
