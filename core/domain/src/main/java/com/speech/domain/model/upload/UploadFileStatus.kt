package com.speech.domain.model.upload

import java.util.Locale

data class UploadFileStatus(
    val progress: Float = 0f,
    val elapsedSeconds: Long = 0L,
    val currentBytes: Long = 0,
    val totalBytes: Long = 0
) {
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
