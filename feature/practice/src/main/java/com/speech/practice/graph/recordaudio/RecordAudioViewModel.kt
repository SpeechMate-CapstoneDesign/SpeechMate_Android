package com.speech.practice.graph.recordaudio

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.practice.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.domain.repository.SpeechRepository
import com.speech.practice.graph.feedback.FeedbackSideEffect
import com.speech.practice.graph.feedback.PlayingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ContainerHost<RecordAudioState, RecordAudioSideEffect>, ViewModel() {

    override val container = container<RecordAudioState, RecordAudioSideEffect>(RecordAudioState())

    private var recordDuration = 0L
    private var timerJob: Job? = null
    private var recorder: MediaRecorder? = null

    fun onIntent(event: RecordAudioIntent) {
        when (event) {
            is RecordAudioIntent.StartRecording -> startRecordAudio()
            is RecordAudioIntent.FinishRecording -> finishRecordAudio()
            is RecordAudioIntent.CancelRecording -> cancelRecordAudio()
            is RecordAudioIntent.PauseRecording -> pauseRecordAudio()
            is RecordAudioIntent.ResumeRecording -> resumeRecordAudio()
            is RecordAudioIntent.OnBackPressed -> onBackPressed()
            is RecordAudioIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is RecordAudioIntent.OnRequestFeedback -> onRequestFeedback()
            is RecordAudioIntent.OnAppBackground -> onAppBackground()
        }
    }

    private fun onBackPressed() = intent {
        val isRecording = state.recordingAudioState == RecordingAudioState.Recording
        if (isRecording) {
            pauseRecordAudio()
        } else {
            postSideEffect(RecordAudioSideEffect.NavigateToBack)
        }
        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "on_back_pressed",
            properties = mutableMapOf("is_recording" to isRecording),
        )
    }

    private fun onAppBackground() {
        pauseRecordAudio()
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingAudioState != RecordingAudioState.Completed || state.audioFile == null) return@intent

        if (!validateSpeechFile(state.audioFile!!.toUri())) {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
            analyticsHelper.trackActionEvent(
                screenName = "record_audio",
                actionName = "request_feedback_invalid_duration",
                properties = mutableMapOf("duration" to recordDuration),
            )
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadFromPath(
                filePath = state.audioFile!!.path,
                speechConfig = state.speechConfig,
                duration = recordDuration.toInt(),
                onProgressUpdate = ::onProgressUpdate,
            )
        }.onSuccess { (speechId, fileUrl) ->
            postSideEffect(
                RecordAudioSideEffect.NavigateToFeedback(
                    speechId = speechId,
                    fileUrl = fileUrl,
                    speechFileType = SpeechFileType.AUDIO,
                    speechConfig = state.speechConfig,
                ),
            )

            analyticsHelper.trackActionEvent(
                screenName = "record_audio",
                actionName = "request_feedback_success",
            )
        }.onFailure {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
            errorHelper.logError(it)
        }.also {
            reduce {
                state.copy(uploadFileStatus = null)
            }
        }
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "set_speech_config",
            properties = mutableMapOf(
                "file_name" to speechConfig.fileName,
                "speech_type" to speechConfig.speechType?.label,
                "audience" to speechConfig.audience?.label,
                "venue" to speechConfig.venue?.label,
            ),
        )
    }

    private fun startRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Ready) return@intent

        reduce {
            state.copy(
                audioFile = File(
                    context.cacheDir,
                    "record_${System.currentTimeMillis()}.mp4",
                ),
            )
        }

        runCatching {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(state.audioFile!!.absolutePath)
                prepare()
                start()
            }
        }.onSuccess {
            startTimer()
            reduce {
                state.copy(recordingAudioState = RecordingAudioState.Recording)
            }

            analyticsHelper.trackActionEvent(
                screenName = "record_audio",
                actionName = "start_recording",
            )
        }.onFailure {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("녹음을 시작하지 못했습니다."))
            errorHelper.logError(it)
        }
    }

    private fun pauseRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Recording) return@intent
        recorder?.pause()
        stopTimer()
        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Paused)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "pause_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    private fun resumeRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Paused) return@intent
        recorder?.resume()
        startTimer()
        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Recording)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "resume_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    private fun finishRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Recording && state.recordingAudioState !is RecordingAudioState.Paused) return@intent

        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Completed)
        }

        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "finish_recording",
            properties = mutableMapOf("record_duration" to recordDuration),
        )
    }

    private fun cancelRecordAudio() = intent {
        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null

        val previousRecordDuration = recordDuration
        recordDuration = 0
        state.audioFile?.let { runCatching { it.delete() } }

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Ready, timeText = "00 : 00 . 00")
        }
        analyticsHelper.trackActionEvent(
            screenName = "record_audio",
            actionName = "cancel_recording",
            properties = mutableMapOf("record_duration" to previousRecordDuration),
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = intent {
            while (state.recordingAudioState is RecordingAudioState.Recording) {
                delay(10)
                recordDuration += 10

                if (recordDuration % 130 == 0L) {
                    reduce {
                        val m = (recordDuration / 1000) / 60
                        val s = (recordDuration / 1000) % 60
                        val ms = ((recordDuration % 1000) / 10).toInt()
                        state.copy(
                            timeText = String.format(Locale.US, "%02d : %02d . %02d", m, s, ms),
                        )
                    }
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

    override fun onCleared() {
        super.onCleared()
        recorder?.release()
        recorder = null
    }
}
