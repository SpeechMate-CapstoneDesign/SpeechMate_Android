package com.speech.domain.model.speech

object SpeechFileRule {
    const val MIN_DURATION_MS = 6000L
    const val MAX_DURATION_MS = 1200000L
    const val MAX_FILE_SIZE_BYTES = 100 * 1024 * 1024L // 100MB
}
