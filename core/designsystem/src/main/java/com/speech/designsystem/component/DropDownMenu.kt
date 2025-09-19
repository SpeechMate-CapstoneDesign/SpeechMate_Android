package com.speech.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.speech.designsystem.R
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.common_ui.util.clickable

data class SMDropdownMenuItem(
    val labelRes: Int,
    val action: () -> Unit,
)

@Composable
fun SMDropDownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismiss: () -> Unit,
    width: Int = 120,
    items: List<SMDropdownMenuItem>,
) {
    if (expanded) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .widthIn(min = width.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                items.forEachIndexed { index, item ->
                    if (index != 0) Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .clickable {
                                onDismiss()
                                item.action()
                            }
                            .widthIn(min = width.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(item.labelRes),
                            style = SpeechMateTheme.typography.bodyXMM,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SMDropDownMenuPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        SMDropDownMenu(
            expanded = true,
            onDismiss = {},
            items = listOf(
                SMDropdownMenuItem(
                    labelRes = R.string.delete,
                    action = {},
                ),
                SMDropdownMenuItem(
                    labelRes = R.string.delete,
                    action = {},
                ),
            ),
        )
    }
}
