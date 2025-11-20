package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.speech.common.util.formatDuration
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.BehaviorGroup
import com.speech.domain.model.speech.NonVerbalAnalysis
import com.speech.domain.model.speech.VerbalAnalysis
import kotlin.time.Duration

@Composable
internal fun NonVerbalAnalysisContent(
    nonVerbalAnalysis: NonVerbalAnalysis,
    seekTo: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        // 헤더 카드
        SMCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(
                        id = R.string.non_verbal_analysis_detected_count,
                        nonVerbalAnalysis.totalCount,
                    ),
                    style = SmTheme.typography.bodyXMSB,
                    color = SmTheme.colors.textPrimary,
                )
            }
        }

        BehaviorGroup.entries.forEach { group ->
            val behaviors = nonVerbalAnalysis.results[group]

            if (!behaviors.isNullOrEmpty()) {
                SMCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = group.emoji,
                                style = SmTheme.typography.bodyXMSB,
                                color = SmTheme.colors.textPrimary,
                            )

                            Text(
                                text = group.label,
                                style = SmTheme.typography.bodyXMSB,
                                color = SmTheme.colors.textPrimary,
                            )
                        }

                        behaviors.forEach { behavior ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${behavior.name} ${behavior.count}회",
                                    style = SmTheme.typography.bodySSB,
                                    color = SmTheme.colors.textPrimary,
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                ) {
                                    behavior.timestamps.forEachIndexed { index, timeRange ->
                                        Row {
                                            Text(
                                                text = formatDuration(timeRange.startTime),
                                                style = SmTheme.typography.bodySM,
                                                color = SmTheme.colors.primaryDefault,
                                                modifier = Modifier.clickable {
                                                    seekTo(timeRange.startTime.inWholeMilliseconds.coerceAtLeast(0))
                                                },
                                            )

                                            Text(
                                                text = " ~ ",
                                                style = SmTheme.typography.bodySM,
                                                color = SmTheme.colors.textSecondary,
                                            )

                                            Text(
                                                text = formatDuration(timeRange.endTime),
                                                style = SmTheme.typography.bodySM,
                                                color = SmTheme.colors.primaryDefault,
                                                modifier = Modifier.clickable {
                                                    seekTo(timeRange.endTime.inWholeMilliseconds)
                                                },
                                            )

                                            if (index != behavior.timestamps.lastIndex) {
                                                Text(
                                                    text = ", ",
                                                    style = SmTheme.typography.bodySM,
                                                    color = SmTheme.colors.textPrimary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


