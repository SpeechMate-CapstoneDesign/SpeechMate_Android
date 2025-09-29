package com.speech.domain.model.upload

import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class UploadFileStatus(
    val elapsedSeconds: Duration = 0.seconds,
    val currentBytes: Long = 0,
    val totalBytes: Long = 0
) {
    val progress = (100 * currentBytes / totalBytes).toFloat()

    val formattedBytes: String
        get() = "${formatBytes(currentBytes)} / ${formatBytes(totalBytes)}"

    private fun formatBytes(bytes: Long): String {
        if (bytes < 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format(Locale.US, "%.1fMB", mb)
            kb >= 1 -> String.format(Locale.US, "%.0fKB", kb)
            else -> "$bytes B"
        }
    }
}
