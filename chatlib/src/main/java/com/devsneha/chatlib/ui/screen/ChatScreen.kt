package com.devsneha.chatlib.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import com.devsneha.chatlib.CompletePreviews
import com.devsneha.chatlib.model.ChatMessage
import com.devsneha.chatlib.model.ChatSender
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.ChatThemes
import com.devsneha.chatlib.theme.LocalChatTheme
import com.devsneha.chatlib.ui.header.ChatHeader
import com.devsneha.chatlib.ui.input.ChatInput
import com.devsneha.chatlib.ui.list.ChatList
import com.devsneha.chatlib.ui.replies.QuickReplies
import com.devsneha.chatlib.ui.typing.TypingIndicator
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import com.devsneha.chatlib.ui.voice.VoiceNoteSheet
import com.devsneha.chatlib.ui.bubble.BubbleVariant

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    recipient: ChatSender? = null,
    quickReplies: List<String> = emptyList(),
    inputValue: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onQuickReply: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onAttachmentClick: () -> Unit = {},
    showActionMenu: Boolean? = null,
    onActionMenuChange: ((Boolean) -> Unit)? = null,
    onVideoCallClick: () -> Unit = {},
    onVoiceCallClick: () -> Unit = {},
    onVoiceRecordClick: () -> Unit = {},
    isTyping: Boolean = false,
    showHeader: Boolean = true,
    showAvatar: Boolean = true,
    showSenderName: Boolean = false,
    showTimestamp: Boolean = true,
    showStatus: Boolean = true,
    showDateSeparators: Boolean = true,
    backgroundImage: Painter? = null,
    backgroundBlur: androidx.compose.ui.unit.Dp = 16.dp,
    backgroundDimAlpha: Float = 0.2f,
    theme: ChatTheme = LocalChatTheme.current,
    modifier: Modifier = Modifier,
    bubbleVariant: BubbleVariant = BubbleVariant.Filled
) {
    val (internalOpen, setInternalOpen) = remember { mutableStateOf(false) }
    val actionsOpen = showActionMenu ?: internalOpen
    val setActionsOpen: (Boolean) -> Unit = { open ->
        setInternalOpen(open)
        onActionMenuChange?.invoke(open)
    }
    val (showRecorder, setShowRecorder) = remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Background layer
        if (backgroundImage != null) {
            Image(
                painter = backgroundImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(backgroundBlur)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundDimAlpha))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                theme.chatBackgroundGradientStart,
                                Color.White
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showHeader && recipient != null) {
                ChatHeader(
                    recipient = recipient,
                    onBackClick = onBackClick,
                    onMenuClick = {
                        // Toggle action menu when three dots menu is clicked
                        setActionsOpen(!actionsOpen)
                        onMenuClick()
                    }
                )
            }

            ChatList(
                messages = messages,
                showAvatar = showAvatar,
                showSenderName = showSenderName,
                showTimestamp = showTimestamp,
                showStatus = showStatus,
                showDateSeparators = showDateSeparators,
                theme = theme,
                bubbleVariant = bubbleVariant,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = if ((quickReplies.isNotEmpty() && !actionsOpen) || isTyping) 120.dp else 80.dp)
            )

            if (isTyping) {
                TypingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Show quick replies only when actions menu is closed and recorder not visible
            AnimatedVisibility(
                visible = quickReplies.isNotEmpty() && !actionsOpen && !showRecorder,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                QuickReplies(
                    replies = quickReplies,
                    onClick = onQuickReply,
                    theme = theme,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (showRecorder) {
                VoiceNoteSheet(
                    onCancel = { setShowRecorder(false) },
                    onSend = {
                        setShowRecorder(false)
                        // You could hook actual audio send here
                    },
                    theme = theme
                )
            } else {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ChatInput(
                        value = inputValue,
                        onValueChange = onInputChange,
                        onSend = onSend,
                        onAttachmentClick = onAttachmentClick,
                        showAttachmentButton = false,
                        showMoreButton = onActionMenuChange != null || showActionMenu != null,
                        onMoreClick = { setActionsOpen(!actionsOpen) },
                        theme = theme,
                        modifier = Modifier.weight(1f)
                    )

                    // Show action buttons when menu is open
                    AnimatedVisibility(
                        visible = actionsOpen,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ActionBubble(icon = Icons.Filled.Videocam, onClick = onVideoCallClick)
                            ActionBubble(icon = Icons.Filled.Call, onClick = onVoiceCallClick)
                            ActionBubble(icon = Icons.Filled.Mic, onClick = {
                                setActionsOpen(false)
                                setShowRecorder(true)
                                onVoiceRecordClick()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionBubble(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .shadow(6.dp, CircleShape, clip = false)
            .background(Color.White.copy(alpha = 0.95f), CircleShape)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF777777)
        )
    }
}

@Composable
@Preview
fun ChatScreenPreview() {
    ChatScreen(
        messages = emptyList(),
        recipient = ChatSender(
            id = "1",
            name = "John Doe",
            avatarUrl = null
        ),
        quickReplies = listOf("Yes", "No", "Maybe"),
        inputValue = "",
        onInputChange = {},
        onSend = {},
        onQuickReply = {}
    )
}