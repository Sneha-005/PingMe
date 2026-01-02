package com.devsneha.chatlib.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devsneha.chatlib.model.ChatMessage
import com.devsneha.chatlib.model.ChatMessageType
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.ui.bubble.BubbleVariant
import com.devsneha.chatlib.theme.ChatTypography
import com.devsneha.chatlib.theme.LocalChatTheme
import com.devsneha.chatlib.ui.avatar.ChatAvatar
import com.devsneha.chatlib.ui.status.MessageStatus
import com.devsneha.chatlib.ui.status.MessageStatusIcon
import com.devsneha.chatlib.util.DateFormatter

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    showAvatar: Boolean = true,
    showSenderName: Boolean = false,
    showTimestamp: Boolean = true,
    showStatus: Boolean = true,
    modifier: Modifier = Modifier,
    status: MessageStatus = MessageStatus.SENT,
    theme: ChatTheme = LocalChatTheme.current,
    bubbleVariant: BubbleVariant
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isMine && showAvatar) {
            ChatAvatar(
                name = message.sender.name,
                avatarUrl = message.sender.avatarUrl,
                size = 32.dp,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(max = 280.dp),
            horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
        ) {
            if (!message.isMine && showSenderName) {
                Text(
                    text = message.sender.name,
                    style = ChatTypography.senderName,
                    color = theme.receivedMessageText,
                    modifier = Modifier.padding(bottom = 4.dp, start = 12.dp)
                )
            }

            when (message.type) {
                ChatMessageType.TEXT -> {
                    TextMessageBubble(
                        message = message,
                        showTimestamp = showTimestamp,
                        showStatus = showStatus && message.isMine,
                        status = status,
                        theme = theme,
                        bubbleVariant = bubbleVariant
                    )
                }
                ChatMessageType.IMAGE -> {
                    ImageMessageBubble(
                        message = message,
                        showTimestamp = showTimestamp,
                        showStatus = showStatus && message.isMine,
                        status = status,
                        theme = theme
                    )
                }
                ChatMessageType.VIDEO -> {
                    VideoMessageBubble(
                        message = message,
                        showTimestamp = showTimestamp,
                        showStatus = showStatus && message.isMine,
                        status = status,
                        theme = theme
                    )
                }
                ChatMessageType.AUDIO -> {
                    AudioMessageBubble(
                        message = message,
                        showTimestamp = showTimestamp,
                        showStatus = showStatus && message.isMine,
                        status = status,
                        theme = theme
                    )
                }
                ChatMessageType.FILE -> {
                    FileMessageBubble(
                        message = message,
                        showTimestamp = showTimestamp,
                        showStatus = showStatus && message.isMine,
                        status = status,
                        theme = theme
                    )
                }
            }
        }

        if (message.isMine && showAvatar) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun TextMessageBubble(
    message: ChatMessage,
    showTimestamp: Boolean,
    showStatus: Boolean,
    status: MessageStatus,
    theme: ChatTheme,
    bubbleVariant: BubbleVariant = BubbleVariant.Filled
) {
    val backgroundColor = if (message.isMine) {
        theme.sentMessageBackground
    } else {
        theme.receivedMessageBackground
    }

    val textColor = run {
        if (bubbleVariant == BubbleVariant.Wire) {
            // Auto-contrast text against chat background
            val reference = blend(theme.chatBackgroundGradientStart, theme.chatBackgroundGradientEnd, 0.5f)
            if (isDark(reference)) Color.White else Color.Black
        } else {
            if (message.isMine) theme.sentMessageText else theme.receivedMessageText
        }
    }

    val shape = if (message.isMine) {
        ChatShapes.sentMessageBubble
    } else {
        ChatShapes.receivedMessageBubble
    }

    val wireColor = if (message.isMine) theme.sentMessageBackground else backgroundColor

    Column(
        modifier = Modifier
            .then(
                if (bubbleVariant == BubbleVariant.Filled) {
                    Modifier.background(backgroundColor, shape = shape)
                } else {
                    Modifier
                        .border(width = 2.dp, color = wireColor, shape = shape)
                }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.content,
            style = ChatTypography.messageText,
            color = textColor,
            textAlign = if (message.isMine) TextAlign.End else TextAlign.Start
        )

        if (showTimestamp || showStatus) {
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showTimestamp) {
                    Text(
                        text = DateFormatter.formatTimeOnly(message.timestamp),
                        style = ChatTypography.messageTimestamp,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = if (showStatus) 4.dp else 0.dp)
                    )
                }
                if (showStatus) {
                    MessageStatusIcon(status = status, theme = theme)
                }
            }
        }
    }

    // Decorative tail and dot for wire style
    if (bubbleVariant == BubbleVariant.Wire) {
        TailAndDot(isMine = message.isMine, color = wireColor)
    }
}

// Computes perceived brightness to decide light/dark
private fun isDark(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = 0.299f * r + 0.587f * g + 0.114f * b
    return luminance < 0.5f
}

// Linear blend two colors by t in [0,1]
private fun blend(a: Color, b: Color, t: Float): Color {
    val clamped = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * clamped,
        green = a.green + (b.green - a.green) * clamped,
        blue = a.blue + (b.blue - a.blue) * clamped,
        alpha = a.alpha + (b.alpha - a.alpha) * clamped
    )
}
@Composable
private fun TailAndDot(isMine: Boolean, color: Color) {
    Row(
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isMine) {
            Dot(color)
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Dot(color)
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .padding(top = 4.dp)
            .size(10.dp)
            .background(color = Color.Transparent, shape = CircleShape)
            .border(width = 2.dp, color = color, shape = CircleShape)
    )
}

@Composable
private fun ImageMessageBubble(
    message: ChatMessage,
    showTimestamp: Boolean,
    showStatus: Boolean,
    status: MessageStatus,
    theme: ChatTheme
) {
    val backgroundColor = if (message.isMine) {
        theme.sentMessageBackground
    } else {
        theme.receivedMessageBackground
    }

    val textColor = if (message.isMine) {
        theme.sentMessageText
    } else {
        theme.receivedMessageText
    }

    val shape = if (message.isMine) {
        ChatShapes.sentMessageBubble
    } else {
        ChatShapes.receivedMessageBubble
    }

    Column(
        modifier = Modifier
            .background(backgroundColor, shape = shape)
            .padding(4.dp)
    ) {
        AsyncImage(
            model = message.content,
            contentDescription = "Image message",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        if (showTimestamp || showStatus) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showTimestamp) {
                    Text(
                        text = DateFormatter.formatTimeOnly(message.timestamp),
                        style = ChatTypography.messageTimestamp,
                        color = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = if (showStatus) 4.dp else 0.dp)
                    )
                }
                if (showStatus) {
                    MessageStatusIcon(status = status, theme = theme)
                }
            }
        }
    }
}

@Composable
private fun VideoMessageBubble(
    message: ChatMessage,
    showTimestamp: Boolean,
    showStatus: Boolean,
    status: MessageStatus,
    theme: ChatTheme
) {
    // Similar to ImageMessageBubble but with video player placeholder
    ImageMessageBubble(message, showTimestamp, showStatus, status, theme)
}

@Composable
private fun AudioMessageBubble(
    message: ChatMessage,
    showTimestamp: Boolean,
    showStatus: Boolean,
    status: MessageStatus,
    theme: ChatTheme
) {
    TextMessageBubble(message, showTimestamp, showStatus, status, theme, bubbleVariant = BubbleVariant.Filled)
}

@Composable
private fun FileMessageBubble(
    message: ChatMessage,
    showTimestamp: Boolean,
    showStatus: Boolean,
    status: MessageStatus,
    theme: ChatTheme
) {
    TextMessageBubble(message, showTimestamp, showStatus, status, theme, bubbleVariant = BubbleVariant.Filled)
}

