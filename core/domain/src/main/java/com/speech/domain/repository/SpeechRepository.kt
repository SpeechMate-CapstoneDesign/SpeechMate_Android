package com.speech.domain.repository

import androidx.paging.PagingData
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.upload.UploadFileStatus
import kotlinx.coroutines.flow.Flow
import kotlin.Pair

interface SpeechRepository {
    suspend fun uploadFromUri(
        uriString: String,
        speechConfig: SpeechConfig,
        duration: Int,
        onProgressUpdate: (UploadFileStatus) -> Unit,
    ): Pair<Int, String>

    suspend fun uploadFromPath(
        filePath: String,
        speechConfig: SpeechConfig,
        duration: Int,
        onProgressUpdate: (UploadFileStatus) -> Unit,
    ): Pair<Int, String>

    suspend fun processSpeechToScript(speechId: Int): String
    suspend fun processScriptAnalysis(speechId: Int): ScriptAnalysis
    fun getSpeechFeeds(): Flow<PagingData<SpeechFeed>>

    suspend fun getSpeechConfig(speechId: Int): SpeechDetail
    suspend fun getScript(speechId: Int): String
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysis
    suspend fun getVerbalAnalysis(speechId: Int)
    suspend fun getVideoAnalysis(speechId: Int)
}
