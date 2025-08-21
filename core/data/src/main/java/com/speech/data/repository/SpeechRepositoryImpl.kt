package com.speech.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.speech.common.util.suspendRunCatching
import com.speech.data.util.getExtension
import com.speech.data.util.getMimeType
import com.speech.domain.repository.SpeechRepository
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechDataSource: SpeechDataSource
) : SpeechRepository {
    override suspend fun uploadSpeechFile(uriString: String) {
        val uri = uriString.toUri()
        val contentResolver = context.contentResolver
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val fileExtension = getExtension(contentResolver, uri)
        val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
        val mimeType = when (val type = getMimeType(contentResolver, uri)) {
            "audio/x-wav" -> "audio/wav"
            else -> type
        }

        contentResolver.openInputStream(uri)?.use { inputStream ->
            speechDataSource.uploadSpeechFile(presignedUrl, inputStream, mimeType)

            speechDataSource.uploadSpeechCallback(key).also {
                getSpeechAnalysis(it.speechId)
            }

            contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } ?: throw IllegalStateException("Could not open input stream from uri: $uri")
    }

    override suspend fun getSpeechAnalysis(speechId: Int) {
        speechDataSource.getSpeechToText(speechId)
        speechDataSource.getTextAnalysis(speechId)
    }
}