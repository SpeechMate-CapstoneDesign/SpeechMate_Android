package com.speech.network.adapter

import android.util.Log
import com.speech.network.model.ApiResponse
import com.speech.network.model.error.HttpResponseException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechMateCallAdapterFactory @Inject constructor() : CallAdapter.Factory() {
    override fun get(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(type) != Call::class.java) return null

        val wrapperType = getParameterUpperBound(0, type as ParameterizedType)
        if (getRawType(wrapperType) != ApiResponse::class.java) return null

        return SpeechMateCallAdapter(wrapperType)
    }
}

private class SpeechMateCallAdapter(
    private val resultType: Type,
) : CallAdapter<Any, Call<Any>> {
    override fun responseType(): Type = resultType

    override fun adapt(call: Call<Any>): Call<Any> = SpeechMateCall(call)
}

private class SpeechMateCall<T : Any>(
    private val delegate: Call<T>
) : Call<T> {
    override fun enqueue(callback: Callback<T>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.onResponse(this@SpeechMateCall, response)
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""

                    val exception = HttpResponseException(
                        status = "failure",
                        resultCode = response.code(),
                        msg = errorBody,
                    )

                    callback.onFailure(this@SpeechMateCall, exception)
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                callback.onFailure(this@SpeechMateCall, throwable)
            }
        })
    }

    override fun clone(): Call<T> = SpeechMateCall(delegate.clone())
    override fun execute(): Response<T> =
        throw NotImplementedError("SpeechMateCall doesn't support execute()")

    override fun isExecuted(): Boolean = delegate.isExecuted
    override fun cancel() = delegate.cancel()
    override fun isCanceled(): Boolean = delegate.isCanceled
    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout = delegate.timeout()
}
