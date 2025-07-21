package com.speech.auth.graph.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speech.common.event.EventHelper
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    internal val eventHelper: EventHelper,
) : ViewModel() {
    private val _eventChannel = Channel<OnBoardingEvent>(Channel.BUFFERED)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val _selectedVerbalSkills = MutableStateFlow<List<VerbalSkill>>(emptyList())
    val selectedVerbalSkills = _selectedVerbalSkills.asStateFlow()

    private val _selectedNonVerbalSkills = MutableStateFlow<List<NonVerbalSkill>>(emptyList())
    val selectedNonVerbalSkills = _selectedNonVerbalSkills.asStateFlow()

    val signUpAvailability: StateFlow<Boolean> = combine(
        selectedVerbalSkills,
        selectedNonVerbalSkills
    ) { verbalSkills, nonVerbalSkills ->
        verbalSkills.isNotEmpty() || nonVerbalSkills.isNotEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = false
    )

    fun toggleVerbalSkill(verbalSkill: VerbalSkill) {
        val currentSkills = _selectedVerbalSkills.value

        if (currentSkills.contains(verbalSkill)) {
            _selectedVerbalSkills.value = currentSkills - verbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                _selectedVerbalSkills.value = currentSkills.drop(1) + verbalSkill
            } else {
                _selectedVerbalSkills.value = currentSkills + verbalSkill
            }
        }
    }

    fun toggleNonVerbalSkill(nonVerbalSkill: NonVerbalSkill) {
        val currentSkills = _selectedNonVerbalSkills.value
        if (currentSkills.contains(nonVerbalSkill)) {
            _selectedNonVerbalSkills.value = currentSkills - nonVerbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                _selectedNonVerbalSkills.value = currentSkills.drop(1) + nonVerbalSkill
            } else {
                _selectedNonVerbalSkills.value = currentSkills + nonVerbalSkill
            }
        }
    }

    fun signUp() {

    }

    sealed class OnBoardingEvent {
        data object SignupSuccess : OnBoardingEvent()
        data object SignupFailure : OnBoardingEvent()
    }

    companion object {
        private const val MAX_SKILL_SELECTION = 2
    }
}
