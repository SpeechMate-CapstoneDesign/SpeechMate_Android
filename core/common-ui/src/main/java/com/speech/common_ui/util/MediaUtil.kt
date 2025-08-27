package com.speech.common_ui.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS

object MediaUtil {
    @androidx.annotation.WorkerThread
    fun isDurationValid(context: Context, uri: Uri): Boolean {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, uri)
            val durationMs = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
            durationMs in MIN_DURATION_MS..MAX_DURATION_MS
        } catch (e: Exception) {
            Log.w("MediaUtil", "Failed to read duration for $uri", e)
            false
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }
    }
}