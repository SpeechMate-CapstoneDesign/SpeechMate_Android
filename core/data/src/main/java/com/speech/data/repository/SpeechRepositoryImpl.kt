package com.speech.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.speech.common.util.suspendRunCatching
import com.speech.data.paging.SpeechFeedPagingSource
import com.speech.data.util.getExtension
import com.speech.data.util.getMimeType
import com.speech.domain.model.speech.Script
import com.speech.domain.model.speech.ScriptAnalysis
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechDetail
import com.speech.domain.model.speech.SpeechFeed
import com.speech.domain.model.speech.VerbalAnalysis
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.domain.repository.SpeechRepository
import com.speech.network.source.speech.SpeechDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import com.speech.domain.repository.SpeechRepository.SpeechUpdateEvent

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechDataSource: SpeechDataSource,
) : SpeechRepository {
    private val _speechUpdateEvents = MutableSharedFlow<SpeechUpdateEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val speechUpdateEvents: SharedFlow<SpeechUpdateEvent> = _speechUpdateEvents.asSharedFlow()

    override suspend fun uploadFromUri(
        uriString: String,
        speechConfig: SpeechConfig,
        duration: Int,
        onProgressUpdate: (UploadFileStatus) -> Unit,
    ): Pair<Int, String> {
        val uri = uriString.toUri()
        val contentResolver = context.contentResolver
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )

        try {
            val fileExtension = getExtension(contentResolver, uri)
            val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
            val mimeType = when (val type = getMimeType(contentResolver, uri)) {
                "audio/x-wav" -> "audio/wav"
                else -> type
            }

            speechDataSource.uploadSpeechFile(uri, presignedUrl, mimeType, onProgressUpdate)
            val response = speechDataSource.uploadSpeechCallback(key, duration)
            speechDataSource.updateSpeechConfig(response.speechId, speechConfig)

            _speechUpdateEvents.emit(SpeechUpdateEvent.SpeechAdded)

            return Pair(response.speechId, response.fileUrl)
        } finally {

            contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        }
    }

    override suspend fun uploadFromPath(
        filePath: String,
        speechConfig: SpeechConfig,
        duration: Int,
        onProgressUpdate: (UploadFileStatus) -> Unit,
    ): Pair<Int, String> {
        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalStateException("File does not exist at path: $filePath")
        }

        val fileExtension = file.extension
        val (presignedUrl, key) = speechDataSource.getPresignedUrl(fileExtension.uppercase())
        val mimeType = getMimeType(file)

        speechDataSource.uploadSpeechFile(file, presignedUrl, mimeType, onProgressUpdate)
        val response = speechDataSource.uploadSpeechCallback(key, duration)
        speechDataSource.updateSpeechConfig(response.speechId, speechConfig)

        _speechUpdateEvents.emit(SpeechUpdateEvent.SpeechAdded)

        return Pair(response.speechId, response.fileUrl)
    }

    override suspend fun getSpeechConfig(speechId: Int): SpeechDetail =
        speechDataSource.getSpeechConfig(speechId).toDomain()

    override fun getSpeechFeeds(): Flow<PagingData<SpeechFeed>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                SpeechFeedPagingSource(speechDataSource)
            },
        ).flow
    }


    override suspend fun getScript(speechId: Int): Script =
        speechDataSource.getScript(speechId).toDomain()


    override suspend fun getScriptAnalysis(speechId: Int) =
        speechDataSource.getScriptAnalysis(speechId).toDomain()


    override suspend fun getVerbalAnalysis(speechId: Int): VerbalAnalysis =
        speechDataSource.getVerbalAnalysis(speechId).toDomain()

    override suspend fun getVideoAnalysis(speechId: Int) {

    }

    override suspend fun deleteSpeech(speechId: Int) {
        speechDataSource.deleteSpeech(speechId)
        _speechUpdateEvents.tryEmit(SpeechUpdateEvent.SpeechDeleted(speechId))
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 10
    }
}
