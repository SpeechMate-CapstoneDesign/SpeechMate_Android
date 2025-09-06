package com.speech.common_ui.util

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MAX_FILE_SIZE_BYTES
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.model.speech.SpeechFileType
import java.io.File
import java.io.IOException

object MediaUtil {
    @WorkerThread
    fun getDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            return durationStr?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            Log.w("MediaUtil", "Failed to read duration for $uri", e)
            return 0L
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                Log.e("MediaUtil", "Error releasing MediaMetadataRetriever", e)
            }
        }
    }
    @WorkerThread
    fun isDurationValid(context: Context, uri: Uri): Boolean {
        val durationMs = getDuration(context, uri)
        return durationMs in MIN_DURATION_MS..MAX_DURATION_MS
    }

    @WorkerThread
    fun getSpeechFileType(context: Context, uri: Uri): SpeechFileType {
        // 1. MIME 타입으로 확인
        context.contentResolver.getType(uri)?.let { mimeType ->
            if (mimeType.startsWith("video/")) return SpeechFileType.VIDEO
            if (mimeType.startsWith("audio/")) return SpeechFileType.AUDIO
        }

        // 2. MIME 타입으로 확인 실패 시 MediaMetadataRetriever 사용
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
            if ("yes".equals(hasVideo, ignoreCase = true)) {
                return SpeechFileType.VIDEO
            }
        } catch (e: Exception) {
            Log.w("MediaUtil", "Failed to read metadata, defaulting to AUDIO for $uri", e)
        } finally {
            try {
                retriever.release()
            } catch (_: Exception) {
            }
        }

        return SpeechFileType.AUDIO
    }


}
