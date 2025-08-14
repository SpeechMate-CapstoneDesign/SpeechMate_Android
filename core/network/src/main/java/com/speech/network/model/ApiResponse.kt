package com.speech.network.model


import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: String,
    val resultCode: Int,
    val data: T?,
)

internal fun <T> ApiResponse<T>.getData(): T {
    return data ?: Unit as T
}


