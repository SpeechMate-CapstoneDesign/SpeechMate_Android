package com.speech.domain.repository


interface SpeechRepository {
   suspend fun uploadSpeechFile(uriString : String) : Result<Unit>
}