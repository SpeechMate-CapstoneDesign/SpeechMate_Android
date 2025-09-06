package com.speech.practice.graph.practice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import com.speech.common.util.suspendRunCatching
import com.speech.common_ui.util.MediaUtil
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileRule.MAX_DURATION_MS
import com.speech.domain.model.speech.SpeechFileRule.MIN_DURATION_MS
import com.speech.domain.model.upload.UploadFileStatus
import com.speech.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PracticeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val speechRepository: SpeechRepository,
) : ContainerHost<PracticeState, PracticeSideEffect>, ViewModel() {
    override val container = container<PracticeState, PracticeSideEffect>(PracticeState())

    fun onIntent(event: PracticeIntent) {
        when (event) {
            is PracticeIntent.OnSpeechConfigChange -> setSpeechConfig(event.speechConfig)
            is PracticeIntent.OnUploadSpeechFile -> onUploadSpeechFile(event.uri)
            is PracticeIntent.OnRecordAudioClick -> intent {
                postSideEffect(PracticeSideEffect.NavigateToRecordAudio)
            }

            is PracticeIntent.OnRecordVideoClick -> intent {
                postSideEffect(PracticeSideEffect.NavigateToRecordVideo)
            }
        }
    }

    private fun validateSpeechFile(uri: Uri): Boolean = MediaUtil.isDurationValid(context, uri)

    fun setSpeechConfig(speechConfig: SpeechConfig) = intent {
        reduce {
            state.copy(speechConfig = speechConfig)
        }
    }

    fun onUploadSpeechFile(uri: Uri) = intent {
        if (!validateSpeechFile(uri)) {
            postSideEffect(PracticeSideEffect.ShowSnackBar("발표 파일은 1분이상 20분 이하만 업로드 가능합니다."))
            return@intent
        }

        reduce {
            state.copy(isUploadingFile = true)
        }

        val speechFileType = MediaUtil.getSpeechFileType(context, uri)

        suspendRunCatching {
            speechRepository.uploadFromUri(
                uri.toString(), state.speechConfig, MediaUtil.getDuration(context, uri).toInt(),
                onProgressUpdate = ::onProgressUpdate,
            )
        }.onSuccess { (speechId, fileUrl) ->
            postSideEffect(
                PracticeSideEffect.NavigateToFeedback(
                    speechId = speechId,
                    fileUrl = fileUrl,
                    speechFileType = speechFileType,
                    speechConfig = state.speechConfig,
                ),
            )
        }.onFailure {
            postSideEffect(PracticeSideEffect.ShowSnackBar("발표 파일 업로드에 실패했습니다."))
        }.also {
            reduce {
                state.copy(isUploadingFile = false, speechConfig = SpeechConfig(), uploadFileStatus = null)
            }
        }
    }

    private fun onProgressUpdate(status: UploadFileStatus) = intent {
        reduce {
            state.copy(uploadFileStatus = status)
        }
    }

//    @OptIn(UnstableApi::class)
//    private fun changeVideoResolution(
//        inputVideoUri: Uri,
//        outputVideoFile: File,
//        targetHeight: Int = 480,
//        onResult: (resultUri: Uri?) -> Unit,
//    ) {
//        val listener = object : Transformer.Listener {
//            override fun onCompleted(composition: Composition, exportResult: ExportResult) {
//                onResult(Uri.fromFile(outputVideoFile))
//            }
//
//            override fun onError(
//                composition: Composition,
//                exportResult: ExportResult,
//                exportException: ExportException,
//            ) {
//                Log.e("PracticeViewModel", "Video transformation failed.", exportException)
//            }
//        }
//
//        val mediaItem = EditedMediaItem.Builder(MediaItem.fromUri(inputVideoUri))
//            .setEffects(
//                Effects(
//                    emptyList(),
//                    listOf(Presentation.createForHeight(targetHeight)),
//                ),
//            ).build()
//
//        Transformer.Builder(context)
//            .setVideoMimeType(MimeTypes.VIDEO_H264)
//            .setAudioMimeType(MimeTypes.AUDIO_AAC)
//            .addListener(listener)
//            .build()
//            .start(mediaItem, outputVideoFile.absolutePath)
//
//    }
}


