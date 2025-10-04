package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.speech.common.util.formatDuration
import com.speech.common_ui.util.clickable
import com.speech.designsystem.R
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.VerbalAnalysis
import kotlin.time.Duration

@Composable
internal fun VerbalAnalysisContent(
    duration: Duration,
    verbalAnalysis: VerbalAnalysis,
    seekTo: (Long) -> Unit,
) {
    val wpm = verbalAnalysis.wordCount / duration.inWholeMinutes
    val fillers = verbalAnalysis.fillers
    val totalFillerCount = fillers.sumOf { it.timestamps.size }
    val repeatedWords = verbalAnalysis.repeatedWords
    val silences = verbalAnalysis.silences

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
                    text = stringResource(R.string.basic_statistics),
                    style = SmTheme.typography.bodyXMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(verbalAnalysis.wordCount.toString(), style = SmTheme.typography.headingSB, color = SmTheme.colors.textPrimary)

                        Spacer(Modifier.height(2.dp))

                        Text(stringResource(R.string.total_word_count), style = SmTheme.typography.bodyXSM, color = SmTheme.colors.textSecondary)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(verbalAnalysis.syllableCount.toString(), style = SmTheme.typography.headingSB, color = SmTheme.colors.textPrimary)

                        Spacer(Modifier.height(2.dp))

                        Text(stringResource(R.string.total_syllable_count), style = SmTheme.typography.bodyXSM, color = SmTheme.colors.textSecondary)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(verbalAnalysis.wordCount.toString(), style = SmTheme.typography.headingSB, color = SmTheme.colors.primaryDefault)

                        Spacer(Modifier.height(2.dp))

                        Text(stringResource(R.string.speaking_speed_wpm), style = SmTheme.typography.bodyXSM, color = SmTheme.colors.textSecondary)
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
                    text = stringResource(R.string.filler_word_analysis),
                    style = SmTheme.typography.bodyXMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "총 ${totalFillerCount}회 사용",
                    style = SmTheme.typography.bodyXSM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(6.dp))

                fillers.forEachIndexed { index, filler ->
                    Text("\"${filler.word}\" - ${filler.timestamps.size}회", style = SmTheme.typography.bodyXMM, color = SmTheme.colors.textPrimary)

                    Spacer(Modifier.height(4.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        filler.timestamps.forEachIndexed { timestampIndex, timestamp ->
                            Text(
                                formatDuration(timestamp),
                                color = SmTheme.colors.primaryDefault,
                                modifier = Modifier.clickable {
                                    seekTo(timestamp.inWholeMilliseconds)
                                },
                            )

                            if (timestampIndex != filler.timestamps.lastIndex) {
                                Text(
                                    text = ",",
                                    color = SmTheme.colors.textPrimary,
                                )
                            }
                        }
                    }

                    if (index != fillers.lastIndex) {
                        Spacer(Modifier.height(4.dp))
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.repeated_words),
                    style = SmTheme.typography.bodyXMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Spacer(Modifier.height(8.dp))

                repeatedWords.forEach { repeatedWord ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("\"${repeatedWord.word}\"", style = SmTheme.typography.bodyXMM, color = SmTheme.colors.textPrimary)

                        Spacer(Modifier.weight(1f))

                        Text("${repeatedWord.count}회", style = SmTheme.typography.headingXSB, color = SmTheme.colors.textPrimary)
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
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.silence_sections),
                    style = SmTheme.typography.bodyXMSB,
                    color = SmTheme.colors.textPrimary,
                )

                Text(
                    text = "총 ${silences.size}개",
                    style = SmTheme.typography.bodyXSM,
                    color = SmTheme.colors.textSecondary,
                )

                silences.forEach { silence ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SmTheme.colors.gray.copy(0.1f)),
                    ) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                val silenceDuration = silence.endTime - silence.startTime
                                val durationInSeconds = silenceDuration.inWholeMilliseconds / 100.0
                                val formattedDuration = "%.1f초".format(durationInSeconds / 10.0)

                                Text(
                                    text = formattedDuration,
                                    style = SmTheme.typography.headingXSB,
                                    color = SmTheme.colors.textPrimary,
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "${formatDuration(silence.startTime)} ~ ${formatDuration(silence.endTime)}",
                                    style = SmTheme.typography.bodySM,
                                    color = SmTheme.colors.primaryDefault,
                                    modifier = Modifier.clickable {
                                        seekTo(silence.startTime.inWholeMilliseconds)
                                    },
                                )
                            }

                            Spacer(Modifier.height(3.dp))

                            Text(
                                text = "${silence.wordBefore}...${silence.wordAfter}",
                                style = SmTheme.typography.bodyXMM,
                                color = SmTheme.colors.textPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}
