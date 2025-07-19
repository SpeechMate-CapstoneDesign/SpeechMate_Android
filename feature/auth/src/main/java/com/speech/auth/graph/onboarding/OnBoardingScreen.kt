package com.speech.auth.graph.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import com.speech.auth.graph.onboarding.OnBoardingViewModel.OnBoardingEvent
import com.speech.common.event.SpeechMateEvent
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
        onVerbalSkillClick = {},
        onNonVerbalSkillClick = { }
    )
}


@Composable
fun OnBoardingScreen(
    onVerbalSkillClick: (VerbalSkill) -> Unit,
    onNonVerbalSkillClick: (NonVerbalSkill) -> Unit
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

            Text(
                "발표 실력을 키우고 싶은 부분을 선택해주세요!",
                style = SpeechMateTheme.typography.bodyXMM,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("언어적 목표 \uD83D\uDDE3\uFE0F", style = SpeechMateTheme.typography.headingSB)
            }

            Spacer(modifier = Modifier.height(10.dp))

            VerbalSkill.entries.sortedBy { it == VerbalSkill.OTHER }.forEach { skill ->
                VerbalSkillButton(verbalSkill = skill, isSelected = false, onClick = {})

                Spacer(modifier = Modifier.height(5.dp))
            }

            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Text("비언어적 목표 \uD83E\uDDCD", style = SpeechMateTheme.typography.headingSB)
            }

            Spacer(modifier = Modifier.height(10.dp))

            NonVerbalSkill.entries.sortedBy { it == NonVerbalSkill.OTHER }.forEach { skill ->
                NonVerbalSkillButton(nonVerbalSkill = skill, isSelected = false, onClick = {})

                Spacer(modifier = Modifier.height(5.dp))
            }

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {

                },
                colors =  ButtonDefaults.buttonColors(
                    PrimaryDefault
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
        Text(verbalSkill.label, style = SpeechMateTheme.typography.bodyXMM)
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
        Text(nonVerbalSkill.label, style = SpeechMateTheme.typography.bodyXMM)
    }
}


@Preview
@Composable
private fun OnBoardingScreenPreview() {
    OnBoardingScreen(
        onVerbalSkillClick = {},
        onNonVerbalSkillClick = {}
    )
}
