package com.speech.common.util

fun String.ellipsize(maxLength: Int): String {
    return if (length > maxLength) "${take(maxLength)}.." else this
}
