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
        val countingSink = CountingSink(
            delegate = sink,
            totalBytes = contentLength,
            onProgressUpdate = { bytesWritten ->
                val elapsedTimeMills = System.currentTimeMillis() - startTime
                listener(
                    UploadFileStatus(
                        elapsedSeconds = elapsedTimeMills.milliseconds,
                        currentBytes = bytesWritten,
                        totalBytes = contentLength,
                    ),
                )
            },
        )

        source.use { inputSource ->
            val bufferedSink = countingSink.buffer()
            bufferedSink.writeAll(inputSource)
            bufferedSink.flush()
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
        val countingSink = CountingSink(
            delegate = sink,
            totalBytes = contentLength,
            onProgressUpdate = { bytesWritten ->
                val elapsedTimeMills = System.currentTimeMillis() - startTime
                listener(
                    UploadFileStatus(
                        elapsedSeconds = elapsedTimeMills.milliseconds,
                        currentBytes = bytesWritten,
                        totalBytes = contentLength,
                    ),
                )
            },
        )

        source.use { inputSource ->
            val bufferedSink = countingSink.buffer()
            bufferedSink.writeAll(inputSource)
            bufferedSink.flush()
        }
    }
}

private class CountingSink(
    delegate: Sink,
    private val totalBytes: Long,
    private val onProgressUpdate: (bytesWritten: Long) -> Unit,
) : ForwardingSink(delegate) {
    private var totalBytesWritten = 0L
    private var lastReportedBytes = 0L
    private val updateInterval = 100 * 1024L
    private var finalProgressReported = false

    override fun write(source: Buffer, byteCount: Long) {
        super.write(source, byteCount)
        totalBytesWritten += byteCount

        if (totalBytesWritten - lastReportedBytes >= updateInterval || totalBytesWritten == totalBytes) {
            lastReportedBytes = totalBytesWritten
            onProgressUpdate(totalBytesWritten)
            if (totalBytesWritten == totalBytes) finalProgressReported = true
        }
    }

    override fun flush() {
        super.flush()
        if (!finalProgressReported && totalBytesWritten > lastReportedBytes) {
            lastReportedBytes = totalBytesWritten
            onProgressUpdate(totalBytesWritten)
        }
    }
}
