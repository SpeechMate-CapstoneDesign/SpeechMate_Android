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
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var _elapsedTime = 0L
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

            is RecordAudioIntent.OnRequestFeedback -> intent {
                onRequestFeedback()
            }
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean {
        val durationMs = MediaMetadataRetriever().use { retriever ->
            retriever.setDataSource(context, uri)
            retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L
        }

        return durationMs >= MIN_DURATION_MS && durationMs <= MAX_DURATION_MS
    }

    private fun onRequestFeedback() = intent {
        if (state.recordingState != RecordingState.Completed) return@intent

        if (!validateSpeechFile(audioFile.toUri())) {
            postSideEffect(RecordAudioSideEffect.ShowSnackBar("발표 파일은 1분 이상 20분 이하만 피드백 가능합니다."))
            return@intent
        }
    }

    private fun recordAudio() = intent {
        if (state.recordingState !is RecordingState.Ready) return@intent

        audioFile = File(
            context.cacheDir,
            "record_${System.currentTimeMillis()}.m4a"
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
            state.copy(recordingState = RecordingState.Recording)
        }
    }

    private fun pauseAudio() = intent {
        if (state.recordingState !is RecordingState.Recording) return@intent
        recorder?.pause()
        stopTimer()
        reduce {
            state.copy(recordingState = RecordingState.Paused)
        }
    }

    private fun resumeAudio() = intent {
        if (state.recordingState !is RecordingState.Paused) return@intent
        recorder?.resume()
        startTimer()
        reduce {
            state.copy(recordingState = RecordingState.Recording)
        }
    }

    private fun finishRecordAudio() = intent {
        if (state.recordingState !is RecordingState.Recording && state.recordingState !is RecordingState.Paused) return@intent

        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null

        reduce {
            state.copy(recordingState = RecordingState.Completed)
        }
    }

    private fun cancelAudio() = intent {
        stopTimer()
        recorder?.stop()
        recorder?.release()
        recorder = null
        audioFile.delete()
        _elapsedTime = 0

        reduce {
            state.copy(recordingState = RecordingState.Ready, timeText = "00 : 00 . 00")
        }
    }

    private fun startTimer() = intent {
        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (state.recordingState is RecordingState.Recording) {
                delay(10)
                _elapsedTime += 10
                if (_elapsedTime % 130 == 0L) {
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
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
}