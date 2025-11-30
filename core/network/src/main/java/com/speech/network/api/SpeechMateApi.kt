package com.speech.network.api

import com.speech.network.model.ApiResponse
import com.speech.network.model.auth.LoginKakaoRequest
import com.speech.network.model.auth.LoginKakaoResponse
import com.speech.network.model.auth.ReissueTokenRequest
import com.speech.network.model.auth.ReissueTokenResponse
import com.speech.network.model.auth.SignUpKakaoRequest
import com.speech.network.model.auth.SignUpKakaoResponse
import com.speech.network.model.notification.PostDeviceTokenRequest
import com.speech.network.model.speech.GetNonVerbalAnalysisResponse
import com.speech.network.model.speech.GetPresignedUrlResponse
import com.speech.network.model.speech.GetSpeechConfigResponse
import com.speech.network.model.speech.GetSpeechFeedResponse
import com.speech.network.model.speech.GetVerbalAnalysisResponse
import com.speech.network.model.speech.ScriptAnalysisResponse
import com.speech.network.model.speech.ScriptResponse
import com.speech.network.model.speech.UpdateSpeechConfigRequest
import com.speech.network.model.speech.UploadSpeechCallbackResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
    suspend fun logout()

    @POST("/api/auth/withdraw")
    suspend fun unRegisterUser()

    // Speech Analysis
    @GET("/api/speech/myFeed")
    suspend fun getSpeechFeeds(
        @Query("lastSpeechId") lastSpeechId: Int,
        @Query("limit") limit: Int,
        @Query("sortType") sortType: String = "LATEST",
    ): ApiResponse<GetSpeechFeedResponse>

    @POST("/api/speech/presignedWithS3")
    suspend fun getPresignedUrl(@Query("fileExtension") fileExtension: String): ApiResponse<GetPresignedUrlResponse>

    @POST("/api/speech/s3-callback")
    suspend fun uploadSpeechCallback(
        @Query("fileKey") fileKey: String,
        @Query("durationSeconds") duration: Int,
    ): ApiResponse<UploadSpeechCallbackResponse>

    @PUT("/api/speech/metadata/{speechId}")
    suspend fun updateSpeechConfig(@Path("speechId") speechId: Int, @Body updateSpeechConfigRequest: UpdateSpeechConfigRequest): ApiResponse<Unit>

    @GET("/api/speech/{speechId}/speechConfig")
    suspend fun getSpeechConfig(@Path("speechId") speechId: Int) : ApiResponse<GetSpeechConfigResponse>

    @POST("/api/speech/rtzrstt/{speechId}")
    suspend fun getScript(@Path("speechId") speechId: Int): ApiResponse<ScriptResponse>

    @POST("/api/speech/analyze/{speechId}")
    suspend fun getScriptAnalysis(@Path("speechId") speechId: Int): ApiResponse<ScriptAnalysisResponse>

    @GET("/api/speech/{speechId}/verbalAnalysis")
    suspend fun getVerbalAnalysis(@Path("speechId") speechId: Int) : ApiResponse<GetVerbalAnalysisResponse>

    @POST("/api/speech/nonverbal/{speechId}")
    suspend fun getNonVerbalAnalysis(@Path("speechId") speechId: Int) : ApiResponse<GetNonVerbalAnalysisResponse>

    @DELETE("/api/speech/delete/{speechId}")
    suspend fun deleteSpeech(@Path("speechId") speechId: Int)

    // Notification
    @POST("/api/fcm/register")
    suspend fun postDeviceToken(
        @Body postDeviceTokenRequest: PostDeviceTokenRequest
    )
}
