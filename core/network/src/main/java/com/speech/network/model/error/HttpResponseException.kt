package com.speech.network.model.error

data class HttpResponseException(
    val status: String,
    val resultCode: Int,
    val msg: String? = null,
    override val cause: Throwable? = null,
) : Exception(msg, cause)
