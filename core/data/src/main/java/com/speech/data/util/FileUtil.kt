package com.speech.data.util

import android.webkit.MimeTypeMap
import java.io.File

fun getMimeType(file : File) : String {
    val fileExtension = file.extension
    val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) ?: throw IllegalArgumentException("Unknown File")
    return when (mimeType) {
        "audio/x-wav" -> "audio/wav"
        else -> mimeType
    }
}