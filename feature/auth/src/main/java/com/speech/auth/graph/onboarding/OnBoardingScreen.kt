package com.speech.auth.graph.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.R
import com.speech.designsystem.component.SMOutlineButton
import com.speech.designsystem.theme.SmTheme
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun OnBoardingRoute(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    navigateToPractice: () -> Unit,
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is OnBoardingSideEffect.ShowSnackBar -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(sideEffect.message)
                }
            }

            is OnBoardingSideEffect.NavigateToPractice -> {
                navigateToPractice()
            }
        }
    }

    OnBoardingScreen(
        state = state,
        onVerbalSkillClick = { viewModel.onIntent(OnBoardingIntent.ToggleVerbalSkill(it)) },
        onNonVerbalSkillClick = { viewModel.onIntent(OnBoardingIntent.ToggleNonVerbalSkill(it)) },
        signUp = viewModel::signUp,
    )
}

@Composable
fun OnBoardingScreen(
    state: OnBoardingState,
    onVerbalSkillClick: (VerbalSkill) -> Unit,
    onNonVerbalSkillClick: (NonVerbalSkill) -> Unit,
    signUp: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 83.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.onboarding_title),
                        style = SmTheme.typography.headingMB,
                        color = SmTheme.colors.textPrimary,
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(id = R.string.onboarding_description),
                        style = SmTheme.typography.bodyXMM,
                        color = SmTheme.colors.primaryDefault,
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = stringResource(id = R.string.verbal_goal),
                        style = SmTheme.typography.headingSB,
                        color = SmTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                VerbalSkill.entries.forEach { skill ->
                    val isSelected = state.selectedVerbalSkills.contains(skill)

                    SMOutlineButton(
                        modifier = Modifier.fillMaxWidth(),
                        isSelected = isSelected,
                        onClick = { onVerbalSkillClick(skill) },
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            Text(skill.label, style = SmTheme.typography.bodySM, color = if(isSelected) SmTheme.colors.primaryDefault else SmTheme.colors.textSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = stringResource(id = R.string.non_verbal_goal),
                        style = SmTheme.typography.headingSB,
                        color = SmTheme.colors.textPrimary,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                NonVerbalSkill.entries.forEach { skill ->
                    SMOutlineButton(
                        modifier = Modifier.fillMaxWidth(),
                        isSelected = state.selectedNonVerbalSkills.contains(skill),
                        onClick = { onNonVerbalSkillClick(skill) },
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                            Text(skill.label, style = SmTheme.typography.bodySM)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(Modifier.height(25.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SmTheme.colors.surface)
                .padding(bottom = 12.dp)
                .align(Alignment.BottomCenter),
        ) {
            HorizontalDivider(
                color = SmTheme.colors.border,
                thickness = 1.dp,
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Button(
                    onClick = {
                        signUp()
                    },
                    enabled = state.signUpAvailable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SmTheme.colors.primaryDefault,
                        disabledContainerColor = SmTheme.colors.primaryLight,
                        contentColor = SmTheme.colors.white,
                        disabledContentColor = SmTheme.colors.white,
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),

                    ) {

                    Text(
                        "${stringResource(R.string.complete)} (${state.selectedVerbalSkills.size + state.selectedNonVerbalSkills.size}개 선택)",
                        color = Color.White,
                        style = SmTheme.typography.bodyXMM,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OnBoardingScreenPreview() {
    OnBoardingScreen(
        state = OnBoardingState(signUpAvailable = true),
        onVerbalSkillClick = {},
        onNonVerbalSkillClick = {},
        signUp = {},
    )
}
