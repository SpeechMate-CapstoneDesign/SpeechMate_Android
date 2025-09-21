package com.speech.mypage.graph.mypage

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.speech.common.util.suspendRunCatching
import com.speech.domain.repository.SpeechRepository
import com.speech.mypage.graph.mypage.MyPageSideEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val speechRepository: SpeechRepository,
) : ContainerHost<MyPageState, MyPageSideEffect>, ViewModel() {
    override val container = container<MyPageState, MyPageSideEffect>(MyPageState())

    fun onIntent(event: MyPageIntent) {
        when (event) {
            is MyPageIntent.OnSettingClick -> intent {
                postSideEffect(MyPageSideEffect.NavigateToSetting)
            }

            is MyPageIntent.OnSpeechClick -> intent {
                postSideEffect(
                    NavigateToFeedback(
                        event.speechId,
                        event.fileUrl,
                        event.speechFileType,
                        event.speechConfig,
                    ),
                )
            }


            is MyPageIntent.OnDeleteClick -> onDeleteClick(event.speechId)
        }
    }

    private val cachedSpeechFeeds = speechRepository.getSpeechFeeds().cachedIn(viewModelScope)
    private val deletedSpeechIds = MutableStateFlow<Set<Int>>(emptySet())

    fun getSpeechFeeds() = intent {
        reduce {
            state.copy(
                speechFeeds = combine(
                    cachedSpeechFeeds,
                    deletedSpeechIds,
                ) { pagingData, deletedIds ->
                    pagingData.filter {
                        it.id !in deletedIds
                    }
                },
            )
        }
    }

    fun onRefresh()  {
        deletedSpeechIds.value = emptySet()
    }

    private fun onDeleteClick(speechId: Int) = intent {
        suspendRunCatching {
            speechRepository.deleteSpeech(speechId)
        }.onSuccess {
            deletedSpeechIds.value += speechId
        }.onFailure {
            postSideEffect(ShowSnackbar("스피치 삭제에 실패했습니다."))
        }
    }
}
