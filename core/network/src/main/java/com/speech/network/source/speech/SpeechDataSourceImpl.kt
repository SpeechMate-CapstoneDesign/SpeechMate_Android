package com.speech.network.source.speech

import com.speech.network.api.S3Api
import com.speech.network.api.SpeechMateApi
import com.speech.network.model.getData
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechToTextResponse
import com.speech.network.model.speech.GetTextAnalysisResponse
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
        contentType: String
    ) {
        val mediaType = contentType.toMediaTypeOrNull()
            ?: throw IllegalArgumentException("Invalid media type: $contentType")
        val requestBody = speechFile.readBytes().toRequestBody(mediaType)

        return s3Api.uploadSpeechFile(url, requestBody)
    }

    override suspend fun uploadSpeechCallback(fileKey: String) =
        speechMateApi.uploadSpeechCallback(fileKey).getData()

    override suspend fun getSpeechToText(
        fileKey: String,
        speechId: Int
    ): GetSpeechToTextResponse = speechMateApi.getSpeechToText(fileKey, speechId).getData()

    override suspend fun getTextAnalysis(speechId: Int): GetTextAnalysisResponse =
        speechMateApi.getTextAnalysis(speechId).getData()
}