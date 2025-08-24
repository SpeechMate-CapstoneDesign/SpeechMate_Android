package com.speech.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.speech.common.util.suspendRunCatching
import com.speech.data.util.getExtension
import com.speech.data.util.getMimeType
import com.speech.domain.repository.SpeechRepository
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechDataSource: SpeechDataSource
) : SpeechRepository {
    override suspend fun uploadFromUri(uriString: String) {
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

    override suspend fun uploadFromPath(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalStateException("File does not exist at path: $filePath")
        }

        val fileExtension = file.extension
        Log.d("SpeechRepositoryImpl", "fileExtension: $fileExtension")
        val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
        val mimeType = getMimeType(file)
        Log.d("SpeechRepositoryImpl", "mimeType: $mimeType")

        FileInputStream(file).use { inputStream ->
            speechDataSource.uploadSpeechFile(presignedUrl, inputStream, mimeType)

            speechDataSource.uploadSpeechCallback(key).also {
                getSpeechAnalysis(it.speechId)
            }
        }
    }

    override suspend fun getSpeechAnalysis(speechId: Int) {
        speechDataSource.getSpeechToText(speechId)
        speechDataSource.getTextAnalysis(speechId)
    }
}