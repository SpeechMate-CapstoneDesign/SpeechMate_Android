package com.speech.common_ui.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

fun getExtension(context: Context, uri : Uri): String {
    return if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        val mimeType = context.contentResolver.getType(uri)
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    } else {
        MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    } ?: ""
}