package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.component.SectionDivider
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.ScriptAnalysis

@Composable
internal fun ScriptAnalysisContent(
    scriptAnalysis: ScriptAnalysis,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.keywords),
                    style = SmTheme.typography.bodyMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(4.dp))

                val keywords = scriptAnalysis.keywords.split(", ")

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    keywords.forEach { keyword ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SmTheme.colors.surface)
                                .border(width = 1.dp, color = SmTheme.colors.border, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                        ) {
                            Text(
                                "#${keyword}",
                                style = SmTheme.typography.bodySM,
                                color = SmTheme.colors.primaryDefault,
                            )
                        }
                    }
                }
            }
        }

        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.summary),
                    style = SmTheme.typography.bodyMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = scriptAnalysis.summary,
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.textPrimary,
                )
            }
        }

        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.improvements),
                    style = SmTheme.typography.bodyMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    scriptAnalysis.improvementPoints.forEachIndexed { index, point ->
                        Text(
                            text = "${index + 1}. ${point}",
                            style = SmTheme.typography.bodyXMM,
                            color = SmTheme.colors.textPrimary,
                        )

                        if(index != scriptAnalysis.improvementPoints.lastIndex) {
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.feedback),
                    style = SmTheme.typography.bodyMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = scriptAnalysis.feedback,
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.textPrimary,
                )
            }
        }

        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.expected_questions),
                    style = SmTheme.typography.bodyMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    scriptAnalysis.expectedQuestions.forEachIndexed { index, question ->
                        Text(
                            text = "${index + 1}. ${question}",
                            style = SmTheme.typography.bodyXMM,
                            color = SmTheme.colors.textPrimary,
                        )

                        if(index != scriptAnalysis.expectedQuestions.lastIndex) {
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }
}
