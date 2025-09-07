package com.speech.domain.model.speech

enum class SpeechFileType {
    AUDIO, VIDEO;

    companion object {
        fun fromString(type: String): SpeechFileType =
            entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
                ?: AUDIO
    }
}
