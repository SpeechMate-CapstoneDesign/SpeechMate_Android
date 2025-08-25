package com.speech.practice.graph.recordaudio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.common.util.suspendRunCatching
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordAudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository
) : ContainerHost<RecordAudioState, RecordAudioSideEffect>, ViewModel() {

    override val container = container<RecordAudioState, RecordAudioSideEffect>(RecordAudioState())

    private var _elapsedTime = 6000L
    private var timerJob: Job? = null
    private var recorder: MediaRecorder? = null
    private lateinit var audioFile: File


    fun onIntent(event: RecordAudioIntent) {
        when (event) {
            is RecordAudioIntent.StartRecording -> recordAudio()
            is RecordAudioIntent.FinishRecording -> finishRecordAudio()
            is RecordAudioIntent.CancelRecording -> cancelAudio()
            is RecordAudioIntent.PauseRecording -> pauseAudio()
            is RecordAudioIntent.ResumeRecording -> resumeAudio()
            is RecordAudioIntent.OnBackPressed -> intent {
                postSideEffect(RecordAudioSideEffect.NavigateBack)
            }
            is RecordAudioIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is RecordAudioIntent.OnRequestFeedback -> onRequestFeedback()
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    private fun onRequestFeedback() = intent {
        if (state.recordingAudioState != RecordingAudioState.Completed) return@intent

        if (!validateSpeechFile(audioFile.toUri())) {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
            return@intent
        }

        suspendRunCatching {
            speechRepository.uploadFromPath(
                filePath = audioFile.path,
                speechConfig = state.speechConfig
            )
        }.onSuccess { speechId ->
            postSideEffect(RecordAudioSideEffect.NavigateToFeedback(speechId))
        }.onFailure {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
        }
    }

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
    }

    private fun recordAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Ready) return@intent

        audioFile = File(
            context.cacheDir,
            "record_${System.currentTimeMillis()}.mp4"
        )

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }

        startTimer()

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Recording)
        }
    }

    private fun pauseAudio() = intent {
        if (state.recordingAudioState !is RecordingAudioState.Recording) return@intent
        recorder?.pause()
        stopTimer()
        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Paused)
        }
    }

    private fun resumeAudio() = intent {
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

    private fun cancelAudio() = intent {
        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null
        _elapsedTime = 0

        reduce {
            state.copy(recordingAudioState = RecordingAudioState.Ready, timeText = "00 : 00 . 00")
        }
    }

    private fun startTimer() = intent {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (state.recordingAudioState is RecordingAudioState.Recording) {
                delay(10)
                _elapsedTime += 10
                reduce {
                    val m = (_elapsedTime / 1000) / 60
                    val s = (_elapsedTime / 1000) % 60
                    val ms = ((_elapsedTime % 1000) / 10).toInt()
                    state.copy(
                        timeText = String.format(Locale.US, "%02d : %02d . %02d", m, s, ms)
                    )
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