package com.speech.common_ui.util

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS

object MediaUtil {
    fun isDurationValid(context: Context, uri: Uri): Boolean {
        val durationMs = MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, uri)
            retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
        }

        Log.d("MediaUtil", "durationMs: $durationMs")

        return durationMs in MIN_DURATION_MS..MAX_DURATION_MS
    }
}