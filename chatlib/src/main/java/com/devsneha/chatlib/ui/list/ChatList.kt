package com.devsneha.chatlib.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.model.ChatMessage
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.ui.bubble.BubbleVariant
import com.devsneha.chatlib.theme.LocalChatTheme
import com.devsneha.chatlib.ui.message.ChatMessageItem
import com.devsneha.chatlib.ui.separator.DateSeparator
import com.devsneha.chatlib.util.DateFormatter

@Composable
fun ChatList(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true,
    showSenderName: Boolean = false,
    showTimestamp: Boolean = true,
    showStatus: Boolean = true,
    showDateSeparators: Boolean = true,
    autoScroll: Boolean = true,
    theme: ChatTheme = LocalChatTheme.current,
    bubbleVariant: BubbleVariant = BubbleVariant.Filled
) {
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (autoScroll && messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(messages.size) { index ->
            val message = messages[index]
            val previousMessage = if (index > 0) messages[index - 1] else null
            
            // Show date separator if needed
            if (showDateSeparators && shouldShowDateSeparator(message, previousMessage)) {
                DateSeparator(timestamp = message.timestamp)
            }
            
            ChatMessageItem(
                message = message,
                showAvatar = showAvatar,
                showSenderName = showSenderName,
                showTimestamp = showTimestamp,
                showStatus = showStatus,
                status = message.status,
                theme = theme,
                bubbleVariant = bubbleVariant
            )
        }
    }
}

private fun shouldShowDateSeparator(
    currentMessage: ChatMessage,
    previousMessage: ChatMessage?
): Boolean {
    if (previousMessage == null) return true
    
    val currentDate = DateFormatter.formatSeparatorDate(currentMessage.timestamp)
    val previousDate = DateFormatter.formatSeparatorDate(previousMessage.timestamp)
    
    return currentDate != previousDate
}