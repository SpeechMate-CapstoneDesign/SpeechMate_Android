package com.speech.domain.repository

import com.speech.domain.model.speech.SpeechConfig


interface SpeechRepository {
    suspend fun uploadFromUri(uriString: String, speechConfig: SpeechConfig) : Int
    suspend fun uploadFromPath(filePath: String, speechConfig: SpeechConfig) : Int
    suspend fun getSpeechAnalysis(speechId: Int)
}