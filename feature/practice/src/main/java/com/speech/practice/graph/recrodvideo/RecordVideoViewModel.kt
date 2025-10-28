package com.speech.practice.graph.recrodvideo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.practice.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.domain.repository.SpeechRepository
import com.speech.practice.graph.practice.PracticeSideEffect
import com.speech.practice.graph.recordaudio.RecordAudioSideEffect
import com.speech.practice.graph.recordaudio.RecordingAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.util.Locale
import javax.inject.Inject
import kotlin.use

@HiltViewModel
class RecordVideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ContainerHost<RecordVideoState, RecordVideoSideEffect>, ViewModel() {
    override val container = container<RecordVideoState, RecordVideoSideEffect>(RecordVideoState())

    // CameraX
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    // Recording 관련
    private var recordDuration = 0L
    private var timerJob: Job? = null

    init {
        initializeCamera()
    }

    fun onIntent(event: RecordVideoIntent) {
        when (event) {
            is RecordVideoIntent.StartRecording -> startRecordVideo()
            is RecordVideoIntent.FinishRecording -> finishRecordVideo()
            is RecordVideoIntent.CancelRecording -> cancelRecordVideo()
            is RecordVideoIntent.PauseRecording -> pauseRecordVideo()
            is RecordVideoIntent.ResumeRecording -> resumeRecordVideo()
            is RecordVideoIntent.OnBackPressed -> onBackPressed()
            is RecordVideoIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is RecordVideoIntent.OnRequestFeedback -> onRequestFeedback()
            is RecordVideoIntent.SwitchCamera -> switchCamera()
            is RecordVideoIntent.OnAppBackground -> onAppBackground()
        }
    }

    fun onBackPressed() = intent {
        val isRecording =
            state.recordingVideoState is RecordingVideoState.Recording || state.recordingVideoState is RecordingVideoState.Paused

        if (isRecording) {
            pauseRecordVideo()
        } else {
            postSideEffect(RecordVideoSideEffect.NavigateBack)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "on_back_pressed",
            properties = mutableMapOf("is_recording" to isRecording),
        )
    }

    fun onAppBackground() {
        finishRecordVideo()
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "set_speech_config",
            properties = mutableMapOf(
                "file_name" to speechConfig.fileName,
                "speech_type" to speechConfig.speechType?.label,
                "audience" to speechConfig.audience?.label,
                "venue" to speechConfig.venue?.label,
            ),
        )
    }

    private fun switchCamera() = intent {
        reduce {
            val newSelector = if (state.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            state.copy(cameraSelector = newSelector)
        }
        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "switch_camera",
            properties = mutableMapOf("camera_selector" to state.cameraSelector.toString()),
        )
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingVideoState != RecordingVideoState.Completed || state.videoFile == null) return@intent

        if (!validateSpeechFile(state.videoFile!!.toUri())) {
            postSideEffect(RecordVideoSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))

            analyticsHelper.trackActionEvent(
                screenName = "record_video",
                actionName = "request_feedback_invalid_duration",
                properties = mutableMapOf("duration" to recordDuration),
            )
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadFromPath(
                filePath = state.videoFile!!.path,
                speechConfig = state.speechConfig,
                duration = recordDuration.toInt(),
                onProgressUpdate = ::onProgressUpdate,
            )
        }.onSuccess { (speechId, fileUrl) ->
            postSideEffect(
                RecordVideoSideEffect.NavigateToFeedback(
                    speechId = speechId,
                    fileUrl = fileUrl,
                    speechFileType = SpeechFileType.VIDEO,
                    speechConfig = state.speechConfig,
                ),
            )
            analyticsHelper.trackActionEvent(
                screenName = "record_video",
                actionName = "request_feedback_success",
            )
        }.onFailure {
            postSideEffect(RecordVideoSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
            errorHelper.logError(it)
        }.also {
            reduce {
                state.copy(uploadFileStatus = null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecordVideo() = intent {
        if (recording != null || videoCapture == null || state.recordingVideoState !is RecordingVideoState.Ready) return@intent

        val videoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "video_${System.currentTimeMillis()}.mp4",
        )

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        runCatching {
            val pendingRecording = videoCapture!!.output.prepareRecording(context, outputOptions)
                .withAudioEnabled()

            reduce {
                state.copy(
                    recordingVideoState = RecordingVideoState.Recording,
                    videoFile = videoFile,
                )
            }

            recording = pendingRecording.start(ContextCompat.getMainExecutor(context)) { event ->
                handleVideoRecordEvent(event, videoFile)
            }
        }.onSuccess {
            analyticsHelper.trackActionEvent(
                screenName = "record_video",
                actionName = "start_recording",
            )
        }.onFailure {
            postSideEffect(RecordVideoSideEffect.ShowSnackBar("녹화를 시작하지 못했습니다."))
            errorHelper.logError(it)
        }
    }

    private fun handleVideoRecordEvent(event: VideoRecordEvent, videoFile: File) = intent {
        when (event) {
            is VideoRecordEvent.Start -> {
                startTimer()
            }

            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    Log.e(
                        "RecordVideoViewModel",
                        "Recording failed with error: ${event.error}, cause : ${event.cause}",
                    )
                    errorHelper.logError(event.cause ?: Throwable("Video recording finalize error"))
                    cancelRecordVideo()
                    videoFile.delete()
                }

                recording = null
                recording?.stop()
                stopTimer()
            }


            is VideoRecordEvent.Status -> {
                // Log.d("RecordVideoViewModel", "📊 Recording status: ${event.recordingStats}")
            }

            is VideoRecordEvent.Pause -> {

            }

            is VideoRecordEvent.Resume -> {

            }
        }
    }

    private fun pauseRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Recording) return@intent
        recording?.pause()
        stopTimer()

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Paused)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "pause_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    private fun resumeRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Paused) return@intent
        recording?.resume()
        startTimer()

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Recording)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "resume_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    fun cancelRecordVideo() = intent {
        stopTimer()
        recording?.stop()
        recording = null

        val previousRecordDuration = recordDuration
        recordDuration = 0
        state.videoFile?.delete()

        reduce {
            state.copy(
                timeText = "00 : 00",
                recordingVideoState = RecordingVideoState.Ready,
                videoFile = null,
            )
        }
        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "cancel_recording",
            properties = mutableMapOf("record_duration" to previousRecordDuration),
        )
    }

    fun finishRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Recording && state.recordingVideoState !is RecordingVideoState.Paused) return@intent

        stopTimer()
        recording?.stop()
        recording = null

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Completed)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_video",
            actionName = "finish_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    private fun startTimer() = intent {
        timerJob = viewModelScope.launch {
            while (state.recordingVideoState == RecordingVideoState.Recording) {
                delay(1000)
                recordDuration += 1000
                reduce {
                    val minutes = (recordDuration / 1000) / 60
                    val seconds = (recordDuration / 1000) % 60
                    state.copy(
                        timeText = String.format(Locale.US, "%02d : %02d", minutes, seconds),
                    )
                }

            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun onProgressUpdate(status: UploadFileStatus) = intent {
        reduce {
            state.copy(uploadFileStatus = status)
        }
    }

    private fun initializeCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = cameraProviderFuture.get()

        setupVideoCapture()
    }

    private fun setupVideoCapture() {
        val recorder = Recorder.Builder().setQualitySelector(
            QualitySelector.from(
                Quality.SD,
            ),
        ).build()

        videoCapture = VideoCapture.withOutput(recorder)
    }

    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        cameraSelector: CameraSelector,
    ) {
        cameraProvider?.let { provider ->
            provider.unbindAll()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(surfaceProvider)
            }

            provider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, videoCapture,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraProvider?.unbindAll()
        recording?.stop()
        stopTimer()
    }
}
