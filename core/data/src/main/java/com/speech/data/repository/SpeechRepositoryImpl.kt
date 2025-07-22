package com.speech.data.repository

import android.util.Log
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.SpeechRepository
import com.speech.network.source.speech.SpeechDataSource
import javax.inject.Inject

class SpeechRepositoryImpl @Inject constructor(
    private val speechDataSource: SpeechDataSource
) : SpeechRepository {
    override suspend fun uploadFile(fileExtension: String): Result<Unit> = suspendRunCatching {
        Log.d("speechUrl", fileExtension)
        val presignedUrl = speechDataSource.getPresignedUrl(fileExtension.uppercase()).getOrThrow().data.url
        Log.d("speechUrl", presignedUrl)
        Result.success(Unit)
    }
}