package com.speech.network.source.speech


import com.speech.network.model.speech.GetPresignedUrlResponse


interface SpeechDataSource {
    suspend fun getPresignedUrl(fileExtension: String) : Result<GetPresignedUrlResponse>
}