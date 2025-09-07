package com.speech.network.source.speech


import android.net.Uri
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechConfigResponse
import com.speech.network.model.speech.GetSpeechFeedResponse
import com.speech.network.model.speech.ProcessScriptAnalysisResponse
import com.speech.network.model.speech.ScriptAnalysisResponse
import com.speech.network.model.speech.ScriptResponse
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import java.io.File
import java.io.InputStream

interface SpeechDataSource {
    suspend fun getPresignedUrl(fileExtension: String): GetPresignedUrlResponse
    suspend fun uploadSpeechFile(uri: Uri, presignedUrl: String, contentType: String, onProgressUpdate: (UploadFileStatus) -> Unit)
    suspend fun uploadSpeechFile(file: File, presignedUrl: String, contentType: String, onProgressUpdate: (UploadFileStatus) -> Unit)
    suspend fun uploadSpeechCallback(fileKey: String, duration: Int): UploadSpeechCallbackResponse
    suspend fun updateSpeechConfig(speechId: Int, speechConfig: SpeechConfig)
    suspend fun getSpeechFeeds(lastSpeechId: Int, limit: Int) : GetSpeechFeedResponse
    suspend fun getSpeechConfig(speechId: Int): GetSpeechConfigResponse
    suspend fun getScript(speechId: Int): ScriptResponse
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysisResponse
    suspend fun processSpeechToScript(speechId: Int): ScriptResponse
    suspend fun processScriptAnalysis(speechId: Int): ProcessScriptAnalysisResponse

}
