package com.speech.network.source.speech


import com.speech.domain.model.speech.SpeechConfig
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechConfigResponse
import com.speech.network.model.speech.ProcessScriptAnalysisResponse
import com.speech.network.model.speech.ScriptAnalysisResponse
import com.speech.network.model.speech.ScriptResponse
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import java.io.InputStream

interface SpeechDataSource {
    suspend fun getPresignedUrl(fileExtension: String): GetPresignedUrlResponse
    suspend fun uploadSpeechFile(url: String, speechFile: InputStream, contentType: String)
    suspend fun uploadSpeechCallback(fileKey: String, duration: Int): UploadSpeechCallbackResponse
    suspend fun updateSpeechConfig(speechId: Int, speechConfig: SpeechConfig)
    suspend fun getSpeechConfig(speechId: Int): GetSpeechConfigResponse
    suspend fun getScript(speechId: Int): ScriptResponse
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysisResponse
    suspend fun processSpeechToScript(speechId: Int): ScriptResponse
    suspend fun processScriptAnalysis(speechId: Int): ProcessScriptAnalysisResponse

}
