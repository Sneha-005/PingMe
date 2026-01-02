package com.devsneha.pingme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.CompletePreviews
import com.devsneha.chatlib.model.ChatMessage
import com.devsneha.chatlib.model.ChatMessageType
import com.devsneha.chatlib.model.ChatSender
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.ChatThemes
import com.devsneha.chatlib.theme.ProvideChatTheme
import com.devsneha.chatlib.ui.bubble.BubbleVariant
import com.devsneha.chatlib.ui.screen.ChatScreen
import com.devsneha.chatlib.ui.status.MessageStatus
import com.devsneha.chatlib.ui.theme.ThemeSelector

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewChatScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
@CompletePreviews
fun PreviewChatScreen() {
    // Theme state - change this to test different themes
    var currentTheme by remember { mutableStateOf<ChatTheme>(ChatThemes.DefaultBlue) }
    var showThemeSelector by remember { mutableStateOf(false) }

    // Action menu state - needed for controlling the action buttons
    var showActionMenu by remember { mutableStateOf(false) }

    // Sample recipient
    val recipient = ChatSender(
        id = "1",
        name = "Jane Doe",
        avatarUrl = null
    )

    // Sample current user
    val currentUser = ChatSender(
        id = "2",
        name = "You",
        avatarUrl = null
    )

    // Sample messages
    val sampleMessages = listOf(
        ChatMessage(
            id = "1",
            sender = recipient,
            content = "Which ones?",
            type = ChatMessageType.TEXT,
            timestamp = System.currentTimeMillis() - 300000, // 5 minutes ago
            isMine = false,
            status = MessageStatus.SENT
        ),
        ChatMessage(
            id = "2",
            sender = currentUser,
            content = "Yes, email me",
            type = ChatMessageType.TEXT,
            timestamp = System.currentTimeMillis() - 180000, // 3 minutes ago
            isMine = true,
            status = MessageStatus.READ
        ),
        ChatMessage(
            id = "3",
            sender = recipient,
            content = "Sure, I'll send them over now!",
            type = ChatMessageType.TEXT,
            timestamp = System.currentTimeMillis() - 60000, // 1 minute ago
            isMine = false,
            status = MessageStatus.SENT
        ),
        ChatMessage(
            id = "4",
            sender = currentUser,
            content = "Thanks! Looking forward to reviewing them.",
            type = ChatMessageType.TEXT,
            timestamp = System.currentTimeMillis() - 10000, // 10 seconds ago
            isMine = true,
            status = MessageStatus.DELIVERED
        )
    )

    ProvideChatTheme(theme = currentTheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            ChatScreen(
                messages = sampleMessages,
                recipient = recipient,
                quickReplies = listOf("Hi", "Yes", "No", "Thanks!", "Sure"),
                inputValue = "",
                onInputChange = {},
                showActionMenu = showActionMenu,
                onActionMenuChange = { showActionMenu = it },
                onSend = {},
                onQuickReply = {},
                onVideoCallClick = {
                    // Handle video call
                    println("Video call clicked")
                },
                onVoiceCallClick = {
                    // Handle voice call
                    println("Voice call clicked")
                },
                onVoiceRecordClick = {
                    // Handle voice record
                    println("Voice record clicked")
                },
                theme = currentTheme // Pass theme to update gradient colors
            )

            // Theme selector button - top right corner
            Button(
                onClick = { showThemeSelector = !showThemeSelector },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(if (showThemeSelector) "Close" else "Themes")
            }

            // Theme selector panel with semi-transparent background
            if (showThemeSelector) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 70.dp, start = 8.dp, end = 8.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        ThemeSelector(
                            currentTheme = currentTheme,
                            onThemeSelected = {
                                currentTheme = it
                                showThemeSelector = false // Close selector after selection
                            }
                        )
                    }
                }
            }
        }
    }
}