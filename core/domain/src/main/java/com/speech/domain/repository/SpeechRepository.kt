package com.speech.domain.repository


interface SpeechRepository {
    suspend fun uploadUriFile(uriString: String)
    suspend fun uploadLocalFile(filePath: String)
    suspend fun getSpeechAnalysis(speechId: Int)
}