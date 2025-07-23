package com.speech.network.source.speech

import android.util.Log
import com.speech.network.api.S3Api
import com.speech.network.api.SpeechMateApi
import com.speech.network.model.speech.GetPresignedUrlResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class SpeechDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi,
    private val s3Api: S3Api,
) : SpeechDataSource {
    override suspend fun getPresignedUrl(fileExtension: String): Result<GetPresignedUrlResponse> =
        speechMateApi.getPresignedUrl(fileExtension)

    override suspend fun uploadSpeechFile(url: String, speechFile: InputStream, contentType: String): Result<Unit> {
        val mediaType = contentType.toMediaTypeOrNull() ?: throw IllegalArgumentException("Invalid media type: $contentType")
        val requestBody = speechFile.readBytes().toRequestBody(mediaType)

        Log.d("SpeechDataSourceImpl signature", "$mediaType")

        return s3Api.uploadSpeechFile(url, requestBody)
    }
}