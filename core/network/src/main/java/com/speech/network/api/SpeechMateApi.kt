package com.speech.network.api

import com.google.android.gms.common.api.Api
import com.speech.domain.model.speech.SpeechConfig
import com.speech.network.model.ApiResponse
import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.ReissueTokenRequest
import com.speech.network.model.auth.ReissueTokenResponse
import com.speech.network.model.auth.SignUpKakaoRequest
import com.speech.network.model.auth.SignUpKakaoResponse
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechToTextResponse
import com.speech.network.model.speech.GetTextAnalysisResponse
import com.speech.network.model.speech.UpdateSpeechConfigRequest
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SpeechMateApi {
    // Auth
    @POST("/api/auth/oauth/kakao/login")
    suspend fun loginKakao(@Body loginKakaoRequest: LoginKakaoRequest): ApiResponse<LoginKakaoResponse>

    @POST("/api/auth/oauth/kakao/signup")
    suspend fun signupKakao(@Body signUpKakaoRequest: SignUpKakaoRequest): ApiResponse<SignUpKakaoResponse>

    @POST("/api/auth/reissue")
    suspend fun reissueToken(@Body reissueTokenRequest: ReissueTokenRequest): ApiResponse<ReissueTokenResponse>

    @POST("/api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("/api/auth/withdraw")
    suspend fun unRegisterUser(): ApiResponse<Unit>

    // Speech Analysis
    @POST("/api/speech/presignedWithS3")
    suspend fun getPresignedUrl(@Query("fileExtension") fileExtension: String): ApiResponse<GetPresignedUrlResponse>

    @POST("/api/speech/s3-callback")
    suspend fun uploadSpeechCallback(
        @Query("fileKey") fileKey: String,
        @Query("durationSeconds") duration: Int,
    ): ApiResponse<UploadSpeechCallbackResponse>

    @POST("/api/speech/Whisperstt3/{speechId}")
    suspend fun getSpeechToText(@Path("speechId") speechId: Int): ApiResponse<GetSpeechToTextResponse>

    @POST("/api/speech/analyze/{speechId}")
    suspend fun getTextAnalysis(@Path("speechId") speechId: Int): ApiResponse<GetTextAnalysisResponse>

    @PUT("/api/speech/metadata/{speechId}")
    suspend fun updateSpeechConfig(@Path("speechId") speechId: Int, @Body updateSpeechConfigRequest: UpdateSpeechConfigRequest): ApiResponse<Unit>
}
