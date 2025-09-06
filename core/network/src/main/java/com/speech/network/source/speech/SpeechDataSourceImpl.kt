package com.speech.network.source.speech

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.net.toUri
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.network.api.S3Api
import com.speech.network.api.SpeechMateApi
import com.speech.network.model.getData
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechConfigResponse
import com.speech.network.model.speech.ProcessScriptAnalysisResponse
import com.speech.network.model.speech.ScriptAnalysisResponse
import com.speech.network.model.speech.ScriptResponse
import com.speech.network.model.speech.UpdateSpeechConfigRequest
import com.speech.network.util.FileRequestBody
import com.speech.network.util.UriRequestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.InputStream
import javax.inject.Inject
import okio.source
import java.io.File
import java.io.IOException

class SpeechDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechMateApi: SpeechMateApi,
    private val s3Api: S3Api,
) : SpeechDataSource {
    override suspend fun getPresignedUrl(fileExtension: String): GetPresignedUrlResponse =
        speechMateApi.getPresignedUrl(fileExtension).getData()

    override suspend fun uploadSpeechFile(uri: Uri, presignedUrl: String, contentType: String, onProgressUpdate: (UploadFileStatus) -> Unit) {
        val mediaType = contentType.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $contentType")

        val requestBody = UriRequestBody(
            contentResolver = context.contentResolver,
            uri = uri,
            contentType = mediaType,
            listener = { status ->
                onProgressUpdate(status)
            },
        )

        return s3Api.uploadSpeechFile(presignedUrl, requestBody)
    }

    override suspend fun uploadSpeechFile(file: File, presignedUrl: String, contentType: String, onProgressUpdate: (UploadFileStatus) -> Unit) {
        val mediaType = contentType.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $contentType")

        val requestBody = FileRequestBody(
            file = file,
            contentType = mediaType,
            listener = { status ->
                onProgressUpdate(status)
            },
        )

        return s3Api.uploadSpeechFile(presignedUrl, requestBody)
    }

    override suspend fun uploadSpeechCallback(fileKey: String, duration: Int) =
        speechMateApi.uploadSpeechCallback(fileKey, duration).getData()

    override suspend fun updateSpeechConfig(speechId: Int, speechConfig: SpeechConfig) =
        speechMateApi.updateSpeechConfig(
            speechId,
            updateSpeechConfigRequest =
                UpdateSpeechConfigRequest(
                    title = speechConfig.fileName,
                    presentationContext = speechConfig.speechType!!.name,
                    audience = speechConfig.audience!!.name,
                    location = speechConfig.venue!!.name,
                ),
        ).getData()

    override suspend fun getSpeechConfig(speechId: Int): GetSpeechConfigResponse =
        speechMateApi.getSpeechConfig(speechId).getData()

    override suspend fun getScript(speechId: Int): ScriptResponse =
        speechMateApi.getScript(speechId).getData()

    override suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysisResponse =
        speechMateApi.getScriptAnalysis(speechId).getData()

    override suspend fun processSpeechToScript(speechId: Int): ScriptResponse =
        speechMateApi.processSpeechToScript(speechId).getData()

    override suspend fun processScriptAnalysis(speechId: Int): ProcessScriptAnalysisResponse =
        speechMateApi.processScriptAnalysis(speechId).getData()
}


