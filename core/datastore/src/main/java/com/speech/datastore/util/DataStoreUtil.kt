package com.speech.datastore.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

inline fun <reified T> Gson.fromJsonOrNull(json: String): T? {
    return try {
        val type = object : TypeToken<T>() {}.type
        fromJson<T>(json, type)
    } catch (e: JsonSyntaxException) {
        null
    } catch (e: Exception) {
        null
    }
}