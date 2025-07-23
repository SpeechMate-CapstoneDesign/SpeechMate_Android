package com.speech.network.api

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Url

interface S3Api {
    @PUT
    suspend fun uploadSpeechFile(
        @Url url: String,
        @Body file: RequestBody,
    ): Result<Unit>
}