package com.speech.practice.graph.feedback.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.speech.common_ui.util.clickable
import com.speech.designsystem.theme.SmTheme
import com.speech.domain.model.speech.FeedbackTab


@Composable
fun CustomScrollableTabRow(
    tabs: List<FeedbackTab>,
    selectedTab: FeedbackTab,
    onTabSelected: (FeedbackTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    var textWidth by remember { mutableStateOf(0f) }
    val density = LocalDensity.current

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(SmTheme.colors.border)
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == tab

                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(start = if (index == 0) 20.dp else 10.dp, end = if (index == tabs.lastIndex) 20.dp else 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = tab.label,
                        color = if (isSelected) SmTheme.colors.primaryDefault
                        else SmTheme.colors.textSecondary,
                        style = SmTheme.typography.bodySM,
                        modifier = Modifier.onSizeChanged { size ->
                            if (isSelected) {
                                textWidth = with(density) { size.width.toDp().value }
                            }
                        },
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .width(if (isSelected) (textWidth + 4).dp else 0.dp)
                            .height(2.dp)
                            .background(
                                if (isSelected) SmTheme.colors.primaryDefault
                                else Color.Transparent,
                            ),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(SmTheme.colors.border)
        )
    }
}
