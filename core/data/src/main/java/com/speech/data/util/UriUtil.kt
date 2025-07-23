package com.speech.data.util

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap

fun getMimeType(contentResolver: ContentResolver, uri: Uri): String {
    return contentResolver.getType(uri) ?: throw IllegalArgumentException("Unknown URI")
}

fun getExtension(contentResolver: ContentResolver, uri: Uri): String {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        val mimeType = getMimeType(contentResolver, uri)
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    } else {
        MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    } ?: ""
}