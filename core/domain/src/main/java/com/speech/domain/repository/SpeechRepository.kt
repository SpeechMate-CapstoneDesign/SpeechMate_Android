package com.speech.domain.repository

import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import kotlin.Pair

interface SpeechRepository {
    suspend fun uploadFromUri(uriString: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String>
    suspend fun uploadFromPath(filePath: String, speechConfig: SpeechConfig, duration: Int): Pair<Int, String>
    suspend fun processSpeechToScript(speechId: Int): String
    suspend fun processScriptAnalysis(speechId: Int): ScriptAnalysis

    suspend fun getSpeechConfig(speechId: Int) : SpeechDetail
    suspend fun getScript(speechId: Int): String
    suspend fun getScriptAnalysis(speechId: Int): ScriptAnalysis
    suspend fun getVerbalAnalysis(speechId: Int)
    suspend fun getVideoAnalysis(speechId: Int)
}
