package com.speech.auth.graph.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.speech.auth.graph.onboarding.OnBoardingViewModel.OnBoardingEvent
import com.speech.common_ui.event.SpeechMateEvent
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.domain.model.auth.NonVerbalSkill
import com.speech.domain.model.auth.VerbalSkill

@Composable
internal fun OnBoardingRoute(
    viewModel: OnBoardingViewModel = hiltViewModel(),
    navigateToPractice: () -> Unit
) {
    val selectedVerbalSkills by viewModel.selectedVerbalSkills.collectAsStateWithLifecycle()
    val selectedNonVerbalSkills by viewModel.selectedNonVerbalSkills.collectAsStateWithLifecycle()
    val signUpAvailability by viewModel.signUpAvailability.collectAsStateWithLifecycle()

    // 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                OnBoardingEvent.SignupFailure -> {
                    viewModel.eventHelper.sendEvent(SpeechMateEvent.ShowSnackBar("회원가입에 실패했습니다. 다시 시도해주세요."))
                }

                OnBoardingEvent.SignupSuccess -> {}
            }
        }
    }

    OnBoardingScreen(
        selectedVerbalSkills = selectedVerbalSkills,
        selectedNonVerbalSkills = selectedNonVerbalSkills,
        signUpAvailability = signUpAvailability,
        onVerbalSkillClick = viewModel::toggleVerbalSkill,
        onNonVerbalSkillClick = viewModel::toggleNonVerbalSkill,
        signUp = viewModel::signUp
    )
}


@Composable
fun OnBoardingScreen(
    selectedVerbalSkills: List<VerbalSkill>,
    selectedNonVerbalSkills: List<NonVerbalSkill>,
    signUpAvailability: Boolean,
    onVerbalSkillClick: (VerbalSkill) -> Unit,
    onNonVerbalSkillClick: (NonVerbalSkill) -> Unit,
    signUp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("발표 목표 설정", style = SpeechMateTheme.typography.headingMB)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    "발표 실력을 키우고 싶은 부분을 선택해주세요!",
                    style = SpeechMateTheme.typography.bodyXMM,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("언어적 목표 \uD83D\uDDE3\uFE0F", style = SpeechMateTheme.typography.headingSB)
            }

            Spacer(modifier = Modifier.height(10.dp))

            VerbalSkill.entries.forEach { skill ->
                VerbalSkillButton(
                    verbalSkill = skill,
                    isSelected = selectedVerbalSkills.contains(skill),
                    onClick = { onVerbalSkillClick(skill) })

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("비언어적 목표 \uD83E\uDDCD", style = SpeechMateTheme.typography.headingSB)
            }

            Spacer(modifier = Modifier.height(10.dp))

            NonVerbalSkill.entries.forEach { skill ->
                NonVerbalSkillButton(
                    nonVerbalSkill = skill,
                    isSelected = selectedNonVerbalSkills.contains(skill),
                    onClick = { onNonVerbalSkillClick(skill) })

                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    signUp()
                },
                enabled = signUpAvailability,
                colors = ButtonDefaults.buttonColors(
                    if (signUpAvailability) PrimaryActive else PrimaryDefault
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "완료",
                    color = Color.White,
                    style = SpeechMateTheme.typography.bodyXMM
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun VerbalSkillButton(verbalSkill: VerbalSkill, isSelected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = if (isSelected) PrimaryActive else Color.Gray,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PrimaryActive else Color.Gray
        ), shape = RoundedCornerShape(8.dp)
    ) {
        Text(verbalSkill.label, style = SpeechMateTheme.typography.bodySM)
    }
}

@Composable
private fun NonVerbalSkillButton(
    nonVerbalSkill: NonVerbalSkill,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = if (isSelected) PrimaryActive else Color.Gray,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PrimaryActive else Color.Gray
        ), shape = RoundedCornerShape(8.dp)
    ) {
        Text(nonVerbalSkill.label, style = SpeechMateTheme.typography.bodySM)
    }
}


@Preview
@Composable
private fun OnBoardingScreenPreview() {
    OnBoardingScreen(
        signUpAvailability = true,
        selectedVerbalSkills = emptyList(),
        selectedNonVerbalSkills = emptyList(),
        onVerbalSkillClick = {},
        onNonVerbalSkillClick = {},
        signUp = {},
    )
}
