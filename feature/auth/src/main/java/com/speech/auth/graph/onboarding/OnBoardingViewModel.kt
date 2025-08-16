package com.speech.auth.graph.onboarding

import androidx.lifecycle.ViewModel
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor() : ViewModel(),
    ContainerHost<OnBoardingState, OnBoardingSideEffect> {

    override val container = container<OnBoardingState, OnBoardingSideEffect>(OnBoardingState())

    fun onIntent(event: OnBoardingIntent) = intent {
        when (event) {
            is OnBoardingIntent.ToggleVerbalSkill -> toggleVerbalSkill(event.verbalSkill)
            is OnBoardingIntent.ToggleNonVerbalSkill -> toggleNonVerbalSkill(event.nonVerbalSkill)
        }
    }

    fun toggleVerbalSkill(verbalSkill: VerbalSkill) = intent {
        val currentSkills = state.selectedVerbalSkills

        val newSkills = if (currentSkills.contains(verbalSkill)) {
            currentSkills - verbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                currentSkills.drop(1) + verbalSkill
            } else {
                currentSkills + verbalSkill
            }
        }
        reduce {
            state.copy(
                selectedVerbalSkills = newSkills,
                signUpAvailable = newSkills.isNotEmpty() || state.selectedNonVerbalSkills.isNotEmpty()
            )
        }
    }

    fun toggleNonVerbalSkill(nonVerbalSkill: NonVerbalSkill) = intent {
        val currentSkills = state.selectedNonVerbalSkills
        val newSkills = if (currentSkills.contains(nonVerbalSkill)) {
            currentSkills - nonVerbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                currentSkills.drop(1) + nonVerbalSkill
            } else {
                currentSkills + nonVerbalSkill
            }
        }
        reduce {
            state.copy(
                selectedNonVerbalSkills = newSkills,
                signUpAvailable = newSkills.isNotEmpty() || state.selectedVerbalSkills.isNotEmpty()
            )
        }
    }

    fun signUp() {

    }

    companion object {
        private const val MAX_SKILL_SELECTION = 2
    }
}
