package com.speech.domain.model.speech
enum class SpeechFileType {
    AUDIO, VIDEO;

    companion object {
        fun fromString(type: String): SpeechFileType =
            valueOf(type.uppercase())
    }
}
