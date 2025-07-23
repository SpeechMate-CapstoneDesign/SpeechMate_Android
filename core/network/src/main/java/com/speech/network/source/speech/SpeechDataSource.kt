package com.speech.network.source.speech


import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import java.io.InputStream

interface SpeechDataSource {
    suspend fun getPresignedUrl(fileExtension: String) : Result<GetPresignedUrlResponse>
    suspend fun uploadSpeechFile(url: String, speechFile : InputStream, contentType: String) : Result<Unit>
    suspend fun uploadSpeechCallback(fileKey: String) : Result<UploadSpeechCallbackResponse>
}