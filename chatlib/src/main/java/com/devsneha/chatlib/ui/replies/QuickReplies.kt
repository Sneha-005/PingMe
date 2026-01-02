package com.devsneha.chatlib.ui.replies

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.ChatTypography
import com.devsneha.chatlib.theme.LocalChatTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickReplies(
    replies: List<String>,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    theme: ChatTheme = LocalChatTheme.current
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.forEach { reply ->
            Text(
                text = reply,
                style = ChatTypography.quickReplyText,
                color = theme.quickReplyText,
                modifier = Modifier
                    .clip(ChatShapes.quickReply)
                    .background(theme.quickReplyBackground)
                    .border(1.dp, theme.quickReplyBorder, ChatShapes.quickReply)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onClick(reply) }
            )
        }
    }
}
