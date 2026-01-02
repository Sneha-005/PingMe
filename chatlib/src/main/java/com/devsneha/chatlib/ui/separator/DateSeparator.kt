package com.devsneha.chatlib.ui.separator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTypography
import com.devsneha.chatlib.util.DateFormatter

@Composable
fun DateSeparator(
    timestamp: Long,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ChatColors.separatorBackground,
    textColor: Color = ChatColors.separatorText
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = DateFormatter.formatSeparatorDate(timestamp),
            style = ChatTypography.separatorText,
            color = textColor,
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = ChatShapes.separator
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

