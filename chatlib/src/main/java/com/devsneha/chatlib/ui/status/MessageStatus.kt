package com.devsneha.chatlib.ui.status

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.LocalChatTheme

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    modifier: Modifier = Modifier,
    theme: ChatTheme = LocalChatTheme.current,
    color: Color = when (status) {
        MessageStatus.READ -> theme.messageStatusRead
        MessageStatus.DELIVERED, MessageStatus.SENT, MessageStatus.SENDING -> theme.messageStatusDelivered
    }
) {
    when (status) {
        MessageStatus.SENDING -> {
            // Single check mark for sending
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sending",
                modifier = modifier.size(14.dp),
                tint = color
            )
        }
        MessageStatus.SENT -> {
            // Single check mark for sent
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sent",
                modifier = modifier.size(14.dp),
                tint = color
            )
        }
        MessageStatus.DELIVERED -> {
            // Double check mark for delivered
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Delivered",
                modifier = modifier.size(14.dp),
                tint = color
            )
        }
        MessageStatus.READ -> {
            // Double check mark in blue for read
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Read",
                modifier = modifier.size(14.dp),
                tint = theme.messageStatusRead
            )
        }
    }
}

