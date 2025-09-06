package com.speech.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import com.speech.common.util.suspendRunCatching
import com.speech.data.util.getExtension
import com.speech.data.util.getMimeType
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.repository.SpeechRepository
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechDataSource: SpeechDataSource,
) : SpeechRepository {
    override suspend fun uploadFromUri(uriString: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String> {
        val uri = uriString.toUri()
        val contentResolver = context.contentResolver
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )

        try {
            val fileExtension = getExtension(contentResolver, uri)
            val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
            val mimeType = when (val type = getMimeType(contentResolver, uri)) {
                "audio/x-wav" -> "audio/wav"
                else -> type
            }

            speechDataSource.uploadSpeechFile(uri, presignedUrl, mimeType)

            val response = speechDataSource.uploadSpeechCallback(key, duration)

            speechDataSource.updateSpeechConfig(response.speechId, speechConfig)

            return Pair(response.speechId, response.fileUrl)
        } finally {
            contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    override suspend fun uploadFromPath(filePath: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalStateException("File does not exist at path: $filePath")
        }

        val fileExtension = file.extension
        val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
        val mimeType = getMimeType(file)

        speechDataSource.uploadSpeechFile(file, presignedUrl, mimeType)
        val response = speechDataSource.uploadSpeechCallback(key, duration)
        speechDataSource.updateSpeechConfig(response.speechId, speechConfig)

        return Pair(response.speechId, response.fileUrl)
    }

    override suspend fun processSpeechToScript(speechId: Int): String =
        speechDataSource.processSpeechToScript(speechId).toDomain()


    override suspend fun processScriptAnalysis(speechId: Int): ScriptAnalysis =
        speechDataSource.processScriptAnalysis(speechId).toDomain()

    override suspend fun getSpeechConfig(speechId: Int): SpeechDetail =
        speechDataSource.getSpeechConfig(speechId).toDomain()

    override suspend fun getScript(speechId: Int): String =
        speechDataSource.getScript(speechId).toDomain()


    override suspend fun getScriptAnalysis(speechId: Int) =
        speechDataSource.getScriptAnalysis(speechId).toDomain()


    override suspend fun getVerbalAnalysis(speechId: Int) {

    }

    override suspend fun getVideoAnalysis(speechId: Int) {

    }
}
