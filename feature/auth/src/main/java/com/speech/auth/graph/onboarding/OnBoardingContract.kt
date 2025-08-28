package com.speech.auth.graph.onboarding

import com.speech.auth.graph.login.LoginIntent
import com.speech.common.base.UiIntent
import com.speech.common.base.UiSideEffect
import com.speech.common.base.UiState
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill

data class OnBoardingState(
    val idToken: String = "",
    val selectedVerbalSkills: List<VerbalSkill> = emptyList(),
    val selectedNonVerbalSkills: List<NonVerbalSkill> = emptyList(),
    val signUpAvailable: Boolean = false
) : UiState

sealed class OnBoardingIntent : UiIntent {
    data class ToggleVerbalSkill(val verbalSkill: VerbalSkill) : OnBoardingIntent()
    data class ToggleNonVerbalSkill(val nonVerbalSkill: NonVerbalSkill) : OnBoardingIntent()
    data object OnSignUpClick : OnBoardingIntent()
}

sealed interface OnBoardingSideEffect : UiSideEffect {
    data class ShowSnackBar(val message: String) : OnBoardingSideEffect
    data object NavigateToPractice : OnBoardingSideEffect
}
