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
                val progress = (100 * bytesWritten / contentLength).toFloat()
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                listener(
                    UploadFileStatus(
                        progress = progress,
                        elapsedSeconds = elapsedSeconds,
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
            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            if (contentLength > 0) {
                val progress = (100 * bytesWritten / contentLength).toFloat()
                listener(
                    UploadFileStatus(
                        progress = progress,
                        elapsedSeconds = elapsedSeconds,
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

