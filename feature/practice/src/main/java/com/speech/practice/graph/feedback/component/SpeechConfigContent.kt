package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.component.SMCard
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.SpeechConfig

@Composable
internal fun SpeechConfigContent(
    date: String,
    speechConfig: SpeechConfig,
) {
    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
        SMCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    stringResource(R.string.date),
                    style = SmTheme.typography.bodySM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    date,
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.content,
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
                    stringResource(R.string.speech_name),
                    style = SmTheme.typography.bodySM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    speechConfig.fileName,
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.content,
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
                    stringResource(R.string.speech_context),
                    style = SmTheme.typography.bodySM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    speechConfig.speechType?.label ?: "",
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.content,
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
                    stringResource(R.string.audience),
                    style = SmTheme.typography.bodySM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    speechConfig.audience?.label ?: "",
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.content,
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
                    stringResource(R.string.speech_venue),
                    style = SmTheme.typography.bodySM,
                    color = SmTheme.colors.textSecondary,
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    speechConfig.venue?.label ?: "",
                    style = SmTheme.typography.bodyXMM,
                    color = SmTheme.colors.content,
                )
            }
        }
    }
}
