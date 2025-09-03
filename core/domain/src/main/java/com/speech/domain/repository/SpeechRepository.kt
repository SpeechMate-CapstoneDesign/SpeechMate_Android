package com.speech.domain.repository

import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import kotlin.Pair

interface SpeechRepository {
    suspend fun uploadFromUri(uriString: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String>
    suspend fun uploadFromPath(filePath: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String>
    suspend fun getScript(speechId: Int): String
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysis
    suspend fun getVerbalAnalysis(speechId: Int)
    suspend fun getVideoAnalysis(speechId: Int)
}
