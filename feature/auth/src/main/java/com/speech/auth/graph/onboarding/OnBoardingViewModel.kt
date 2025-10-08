package com.speech.auth.graph.onboarding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.speech.analytics.AnalyticsHelper
import com.speech.analytics.error.ErrorHelper
import com.speech.common.util.suspendRunCatching
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill
import com.speech.domain.repository.AuthRepository
import com.speech.navigation.AuthGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject


@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val analyticsHelper: AnalyticsHelper,
    private val errorHelper: ErrorHelper,
) : ViewModel(),
    ContainerHost<OnBoardingState, OnBoardingSideEffect> {

    override val container = container<OnBoardingState, OnBoardingSideEffect>(OnBoardingState())

    private val routeArgs: AuthGraph.OnBoardingRoute = savedStateHandle.toRoute()
    private val idToken = routeArgs.idToken


    fun onIntent(event: OnBoardingIntent) {
        when (event) {
            is OnBoardingIntent.ToggleVerbalSkill -> toggleVerbalSkill(event.verbalSkill)
            is OnBoardingIntent.ToggleNonVerbalSkill -> toggleNonVerbalSkill(event.nonVerbalSkill)
            is OnBoardingIntent.OnSignUpClick -> signUp()
        }
    }

    fun toggleVerbalSkill(verbalSkill: VerbalSkill) = intent {
        val currentSkills = state.selectedVerbalSkills

        val newSkills = if (currentSkills.contains(verbalSkill)) {
            currentSkills - verbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                analyticsHelper.trackActionEvent(
                    screenName = "onboarding",
                    actionName = "max_verbal_skill_selection_reached",
                    properties = mutableMapOf("dropped_verbal_skill" to state.selectedVerbalSkills.first().label),
                )
                currentSkills.drop(1) + verbalSkill
            } else {
                currentSkills + verbalSkill
            }
        }

        reduce {
            state.copy(
                selectedVerbalSkills = newSkills,
                signUpAvailable = newSkills.isNotEmpty() || state.selectedNonVerbalSkills.isNotEmpty(),
            )
        }
    }

    fun toggleNonVerbalSkill(nonVerbalSkill: NonVerbalSkill) = intent {
        val currentSkills = state.selectedNonVerbalSkills
        val newSkills = if (currentSkills.contains(nonVerbalSkill)) {
            currentSkills - nonVerbalSkill
        } else {
            if (currentSkills.size >= MAX_SKILL_SELECTION) {
                analyticsHelper.trackActionEvent(
                    screenName = "onboarding",
                    actionName = "max_non_verbal_skill_selection_reached",
                    properties = mutableMapOf("dropped_non_verbal_skill" to state.selectedNonVerbalSkills.first().label),
                )
                currentSkills.drop(1) + nonVerbalSkill
            } else {
                currentSkills + nonVerbalSkill
            }
        }
        reduce {
            state.copy(
                selectedNonVerbalSkills = newSkills,
                signUpAvailable = newSkills.isNotEmpty() || state.selectedVerbalSkills.isNotEmpty(),
            )
        }
    }

    fun signUp() = intent {
        suspendRunCatching {
            val selectedSkills =
                state.selectedVerbalSkills.map { it.name } + state.selectedNonVerbalSkills.map { it.name }

            authRepository.signupKakao(
                idToken = idToken,
                skills = selectedSkills,
            )
        }.onSuccess {
            analyticsHelper.trackActionEvent(
                screenName = "onboarding",
                actionName = "sign_up",
                properties = mutableMapOf(
                    "selected_verbal_skills" to state.selectedVerbalSkills.joinToString(", ") { it.label },
                    "selected_non_verbal_skills" to state.selectedNonVerbalSkills.joinToString(", ") { it.label },
                ),
            )

            postSideEffect(OnBoardingSideEffect.NavigateToPractice)
        }.onFailure {
            postSideEffect(OnBoardingSideEffect.ShowSnackBar("회원가입에 실패하였습니다."))
            errorHelper.logError(it)
        }
    }

    companion object {
        private const val MAX_SKILL_SELECTION = 2
    }
}
