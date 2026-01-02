package com.devsneha.chatlib.ui.bubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTypography

@Composable
fun ChatBubble(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ChatColors.receivedMessageBackground,
    textColor: Color = ChatColors.receivedMessageText,
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    isMine: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (alignment == Alignment.End || isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        val shape = if (isMine) {
            ChatShapes.sentMessageBubble
        } else {
            ChatShapes.receivedMessageBubble
        }
        
        Text(
            text = text,
            color = textColor,
            style = ChatTypography.messageText,
            textAlign = if (isMine) TextAlign.End else TextAlign.Start,
            modifier = Modifier
                .background(backgroundColor, shape = shape)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}