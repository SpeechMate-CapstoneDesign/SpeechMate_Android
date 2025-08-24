package com.speech.domain.repository


interface SpeechRepository {
    suspend fun uploadFromUri(uriString: String)
    suspend fun uploadFromPath(filePath: String)
    suspend fun getSpeechAnalysis(speechId: Int)
}