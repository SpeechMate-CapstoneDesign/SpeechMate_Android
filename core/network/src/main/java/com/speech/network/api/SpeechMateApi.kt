package com.speech.network.api

import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.ReissueTokenRequest
import com.speech.network.model.auth.ReissueTokenResponse
import com.speech.network.model.auth.SignUpKakaoRequest
import com.speech.network.model.auth.SignUpKakaoResponse
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface SpeechMateApi {
    // Auth
    @POST("/api/auth/oauth/kakao/login")
    suspend fun loginKakao(@Body loginKakaoRequest: LoginKakaoRequest) : Result<LoginKakaoResponse>

    @POST("/api/auth/oauth/kakao/signup")
    suspend fun signupKakao(@Body signUpKakaoRequest: SignUpKakaoRequest) : Result<SignUpKakaoResponse>

    @POST("/api/auth/reissue")
    suspend fun reissueToken(@Body reissueTokenRequest: ReissueTokenRequest) : Result<ReissueTokenResponse>

    // Speech Analysis
    @POST("/api/speech/presignedWithS3")
    suspend fun getPresignedUrl(@Query("fileExtension") fileExtension: String) : Result<GetPresignedUrlResponse>

    @POST("/api/speech/s3-callback")
    suspend fun uploadSpeechCallback(@Query("fileKey") fileKey: String) : Result<UploadSpeechCallbackResponse>
}