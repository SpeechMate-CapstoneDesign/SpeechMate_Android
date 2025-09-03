package com.speech.practice.graph.recordaudio

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.speech.common.util.suspendRunCatching
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
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
        }
    }

    private fun onBackPressed() = intent {
        val isPlaying = state.recordingAudioState == RecordingAudioState.Recording
        if (isPlaying) pauseRecordAudio()
        else {
            postSideEffect(RecordAudioSideEffect.NavigateToBack)
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingAudioState != RecordingAudioState.Completed || state.audioFile == null) return@intent

        if (!validateSpeechFile(state.audioFile!!.toUri())) {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
            return@intent
        }

        reduce {
            state.copy(isUploadingFile = true)
        }

        suspendRunCatching {
            speechRepository.uploadFromPath(
                filePath = state.audioFile!!.path,
                speechConfig = state.speechConfig,
                duration = recordDuration.toInt(),
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
        }.onFailure {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
        }.also {
            reduce {
                state.copy(isUploadingFile = false)
            }
        }
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
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

        startTimer()

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Recording)
        }
    }

    private fun pauseRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Recording) return@intent
        recorder?.pause()
        stopTimer()
        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Paused)
        }
    }

    private fun resumeRecordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Paused) return@intent
        recorder?.resume()
        startTimer()
        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Recording)
        }
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
    }

    private fun cancelRecordAudio() = intent {
        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null
        recordDuration = 0
        state.audioFile?.let { runCatching { it.delete() } }

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Ready, timeText = "00 : 00 . 00")
        }
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

    override fun onCleared() {
        super.onCleared()
        recorder?.release()
        recorder = null
    }
}
