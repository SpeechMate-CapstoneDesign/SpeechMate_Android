package com.speech.network.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.IOException

internal fun File.asRequestBody(contentType: MediaType? = null): RequestBody {
    return object : RequestBody() {
        override fun contentType(): MediaType? = contentType

        override fun contentLength(): Long = length()

        override fun writeTo(sink: BufferedSink) {
            Log.d("File", "${length()}")
            source().use { source ->
                sink.writeAll(source)
            }
        }
    }
}


internal class StreamingRequestBody(
    private val contentResolver: ContentResolver,
    private val uri: Uri,
    private val contentType: MediaType?,
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = getFileSize(contentResolver, uri)

    override fun writeTo(sink: BufferedSink) {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("Could not open input stream for uri: $uri")

        inputStream.source().use { source ->
            sink.writeAll(source)
        }
    }
}

private fun getFileSize(contentResolver: ContentResolver, uri: Uri): Long {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1) {
            cursor.moveToFirst()
            cursor.getLong(sizeIndex)
        } else {
            0L
        }
    } ?: 0L
}
