package com.speech.network.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.speech.domain.model.upload.UploadFileStatus
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.ForwardingSource
import okio.Sink
import okio.Source
import okio.buffer
import okio.source
import java.io.File
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class FileRequestBody(
    private val file: File,
    private val contentType: MediaType?,
    private val listener: (status: UploadFileStatus) -> Unit,
) : RequestBody() {

    override fun contentType(): MediaType? = contentType
    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val contentLength = contentLength()
        val source = file.source()

        val startTime = System.currentTimeMillis()
        val countingSource = CountingSource(source) { bytesWritten ->
            if (contentLength > 0) {
                val elapsedTimeMills = System.currentTimeMillis() - startTime
                listener(
                    UploadFileStatus(
                        elapsedSeconds = elapsedTimeMills.milliseconds,
                        currentBytes = bytesWritten,
                        totalBytes = contentLength,
                    ),
                )
            }
        }

        countingSource.use {
            sink.writeAll(it)
        }
    }
}


internal class UriRequestBody(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
    private val contentType: MediaType?,
    private val listener: (status: UploadFileStatus) -> Unit,
) : RequestBody() {

    private val contentLength: Long by lazy {
        contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: -1L
    }

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = contentLength

    override fun writeTo(sink: BufferedSink) {
        val source = contentResolver.openInputStream(uri)?.source()
            ?: throw IOException("Failed to open input stream for $uri")

        val startTime = System.currentTimeMillis()
        val countingSource = CountingSource(source) { bytesWritten ->
            val elapsedTimeMills = System.currentTimeMillis() - startTime
            if (contentLength > 0) {
                listener(
                    UploadFileStatus(
                        elapsedSeconds = elapsedTimeMills.milliseconds,
                        currentBytes = bytesWritten,
                        totalBytes = contentLength,
                    ),
                )
            }
        }

        countingSource.use {
            sink.writeAll(it)
        }
    }
}

private class CountingSource(
    delegate: Source,
    private val onProgressUpdate: (bytesRead: Long) -> Unit,
) : ForwardingSource(delegate) {
    private var totalBytesRead = 0L

    @Throws(IOException::class)
    override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)

        if (bytesRead != -1L) {
            totalBytesRead += bytesRead
            onProgressUpdate(totalBytesRead)
        }
        return bytesRead
    }
}

