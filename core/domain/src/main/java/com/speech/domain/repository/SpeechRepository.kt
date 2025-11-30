package com.speech.domain.repository

import androidx.paging.PagingData
import com.speech.domain.model.speech.NonVerbalAnalysis
import com.speech.domain.model.speech.Script
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.VerbalAnalysis
import com.speech.domain.model.upload.UploadFileStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.Pair

interface SpeechRepository {
    val speechUpdateEvents: SharedFlow<SpeechUpdateEvent>
    fun getSpeechFeeds(): Flow<PagingData<SpeechFeed>>
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
    suspend fun getSpeechConfig(speechId: Int) : SpeechDetail
    suspend fun getScript(speechId: Int): Script
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysis
    suspend fun getVerbalAnalysis(speechId: Int) : VerbalAnalysis
<<<<<<< Updated upstream
    suspend fun getNonVerbalAnalysis(speechId: Int) : NonVerbalAnalysis
=======
<<<<<<< Updated upstream
>>>>>>> Stashed changes
    suspend fun getVideoAnalysis(speechId: Int)
=======
    suspend fun getNonVerbalAnalysis(speechId: Int) : NonVerbalAnalysis
>>>>>>> Stashed changes
    suspend fun deleteSpeech(speechId: Int)

    sealed class SpeechUpdateEvent {
        data class SpeechDeleted(val speechId: Int) : SpeechUpdateEvent()
        data object SpeechAdded : SpeechUpdateEvent()
    }
}
