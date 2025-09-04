package com.speech.network.source.speech

import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.network.api.S3Api
import com.speech.network.api.SpeechMateApi
import com.speech.network.model.getData
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechConfigResponse
import com.speech.network.model.speech.ProcessScriptAnalysisResponse
import com.speech.network.model.speech.ScriptAnalysisResponse
import com.speech.network.model.speech.ScriptResponse
import com.speech.network.model.speech.UpdateSpeechConfigRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class SpeechDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi,
    private val s3Api: S3Api,
) : SpeechDataSource {
    override suspend fun getPresignedUrl(fileExtension: String): GetPresignedUrlResponse =
        speechMateApi.getPresignedUrl(fileExtension).getData()

    override suspend fun uploadSpeechFile(
        url: String,
        speechFile: InputStream,
        contentType: String,
    ) {
        val mediaType = contentType.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $contentType")
        val requestBody = speechFile.readBytes().toRequestBody(mediaType)

        return s3Api.uploadSpeechFile(url, requestBody)
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
