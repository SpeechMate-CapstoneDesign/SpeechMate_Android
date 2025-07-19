package com.speech.network.interceptor

import com.speech.network.token.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class SpeechMateInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestBuilder = originRequest.newBuilder()

        if (isAccessTokenUsed(originRequest)) {
            requestBuilder.addHeader(
                "Authorization",
                "Bearer ${runBlocking { tokenManager.getAccessToken() }}"
            )
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun isAccessTokenUsed(request: Request): Boolean {
        return when (request.url.encodedPath) {
            "/api/auth/oauth/kakao/login" -> false
            "/api/auth/oauth/kakao/signup" -> false
//            "/api/v1/token/refresh" -> false
//            "/api/v1/token/expiration" -> false
            else -> true
        }
    }

}