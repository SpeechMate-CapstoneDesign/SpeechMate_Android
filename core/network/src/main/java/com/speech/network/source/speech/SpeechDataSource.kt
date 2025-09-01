package com.speech.network.source.speech


import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechToTextResponse
import com.speech.network.model.speech.GetTextAnalysisResponse
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import java.io.InputStream

interface SpeechDataSource {
    suspend fun getPresignedUrl(fileExtension: String): GetPresignedUrlResponse
    suspend fun uploadSpeechFile(url: String, speechFile: InputStream, contentType: String)
    suspend fun uploadSpeechCallback(fileKey: String, duration : Int): UploadSpeechCallbackResponse
    suspend fun getSpeechToText(speechId: Int): GetSpeechToTextResponse
    suspend fun getTextAnalysis(speechId: Int): GetTextAnalysisResponse
}
