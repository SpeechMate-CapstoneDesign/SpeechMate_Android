package com.speech.network.source.speech

import com.speech.network.api.SpeechMateApi
import com.speech.network.model.speech.GetPresignedUrlResponse
import javax.inject.Inject

class SpeechDataSourceImpl @Inject constructor(
    private val speechMateApi: SpeechMateApi
) : SpeechDataSource {
    override suspend fun getPresignedUrl(fileExtension: String): Result<GetPresignedUrlResponse> =
        speechMateApi.getPresignedUrl(fileExtension)
}