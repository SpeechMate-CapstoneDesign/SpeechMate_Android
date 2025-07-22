package com.speech.domain.repository



interface SpeechRepository {
   suspend fun uploadFile(fileExtension : String) : Result<Unit>
}