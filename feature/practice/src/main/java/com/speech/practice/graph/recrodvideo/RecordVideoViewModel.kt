package com.speech.practice.graph.recrodvideo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
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
import com.speech.common.util.suspendRunCatching
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.repository.SpeechRepository
import com.speech.practice.graph.practice.PracticeSideEffect
import com.speech.practice.graph.recordaudio.RecordAudioSideEffect
import com.speech.practice.graph.recordaudio.RecordingAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject
import kotlin.use

@HiltViewModel
class RecordVideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
) : ContainerHost<RecordVideoState, RecordVideoSideEffect>, ViewModel() {
    override val container = container<RecordVideoState, RecordVideoSideEffect>(RecordVideoState())

    // CameraX 관련 객체들
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    // Recording에 필요
    private var recordDuration = 0L
    private var timerJob: Job? = null

    fun onIntent(event: RecordVideoIntent) {
        when (event) {
            is RecordVideoIntent.OnRequestPermissionFailure -> intent {
                postSideEffect(RecordVideoSideEffect.ShowSnackBar("녹화를 위해 카메라 접근 권한 허용이 필요합니다."))
            }

            is RecordVideoIntent.StartRecording -> startRecordVideo()
            is RecordVideoIntent.FinishRecording -> finishRecordVideo()
            is RecordVideoIntent.CancelRecording -> cancelRecordVideo()
            is RecordVideoIntent.PauseRecording -> pauseRecordVideo()
            is RecordVideoIntent.ResumeRecording -> resumeRecordVideo()
            is RecordVideoIntent.OnBackPressed -> intent {
                postSideEffect(RecordVideoSideEffect.NavigateBack)
            }

            is RecordVideoIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is RecordVideoIntent.OnRequestFeedback -> onRequestFeedback()
        }
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingVideoState != RecordingVideoState.Completed || state.videoFile == null) return@intent

        if (!validateSpeechFile(state.videoFile!!.toUri())) {
            postSideEffect(RecordVideoSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadFromPath(
                filePath = state.videoFile!!.path,
                speechConfig = state.speechConfig
            )
        }.onSuccess { speechId ->
            postSideEffect(RecordVideoSideEffect.NavigateToFeedback(speechId))
        }.onFailure {
            postSideEffect(RecordVideoSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
        }
    }

    private fun initializeCamera(context: Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = cameraProviderFuture.get()

        setupVideoCapture()
    }

    private fun setupVideoCapture() {
        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HD,
                    FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                )
            )
            .build()

        videoCapture = VideoCapture.withOutput(recorder)
    }

    fun bindCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        cameraProvider?.let { provider ->
            provider.unbindAll()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                videoCapture
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startRecordVideo() = intent {
        val videoCapture = videoCapture ?: return@intent

        val videoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "video_${System.currentTimeMillis()}.mp4"
        )

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        recording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .apply {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> intent {
                        reduce {
                            state.copy(
                                recordingVideoState = RecordingVideoState.Recording,
                                videoFile = videoFile
                            )
                        }
                        startTimer()
                    }

                    is VideoRecordEvent.Finalize -> intent {
                        if (event.hasError()) {
                            videoFile.delete()
                        }
                        reduce {
                            state.copy(recordingVideoState = RecordingVideoState.Completed)
                        }
                        stopTimer()
                    }
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
    }

    private fun resumeRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Paused) return@intent
        recording?.resume()
        startTimer()

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Recording)
        }
    }

    fun cancelRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Recording && state.recordingVideoState !is RecordingVideoState.Paused) return@intent

        stopTimer()
        recording?.stop()
        recording = null

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Completed)
        }
    }

    fun finishRecordVideo() = intent {
        if (state.recordingVideoState !is RecordingVideoState.Recording && state.recordingVideoState !is RecordingVideoState.Paused) return@intent

        stopTimer()
        recording?.stop()
        recording = null

        reduce {
            state.copy(recordingVideoState = RecordingVideoState.Completed)
        }
    }

    private fun startTimer() = intent {
        timerJob = viewModelScope.launch {
            while (state.recordingVideoState == RecordingVideoState.Recording) {
                delay(10)
                recordDuration += 10
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        recording?.stop()
        cameraProvider?.unbindAll()

    }

}