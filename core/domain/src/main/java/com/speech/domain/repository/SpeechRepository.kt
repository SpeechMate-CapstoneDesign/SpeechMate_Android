package com.speech.domain.repository

import com.speech.domain.model.speech.SpeechConfig


interface SpeechRepository {
    suspend fun uploadFromUri(uriString: String, speechConfig: SpeechConfig)
    suspend fun uploadFromPath(filePath: String, speechConfig: SpeechConfig)
    suspend fun getSpeechAnalysis(speechId: Int)
}