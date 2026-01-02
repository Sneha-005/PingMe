package com.devsneha.chatlib.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsneha.chatlib.model.Chat
import com.devsneha.chatlib.model.Message
import com.devsneha.chatlib.model.SendMessageRequest
import com.devsneha.chatlib.viewmodel.MessageListViewModel
import com.devsneha.chatlib.viewmodel.SendMessageViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatDetailScreen(
    chat: Chat,
    userId: String,
    userName: String,
    onBackClick: () -> Unit,
    messageListViewModel: MessageListViewModel = hiltViewModel(),
    sendMessageViewModel: SendMessageViewModel = hiltViewModel()
) {
    val messageListState by messageListViewModel.messageListState.collectAsState()
    val sendMessageState by sendMessageViewModel.sendMessageState.collectAsState()
    val typingState by messageListViewModel.typingState.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    LaunchedEffect(chat.id) {
        messageListViewModel.loadMessages(chat.id)
    }
    
    // Auto scroll to bottom when new message is sent
    LaunchedEffect(messageListState.messages.size) {
        if (messageListState.messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }
    
    // Mark messages as read
    LaunchedEffect(messageListState.messages) {
        val unreadIds = messageListState.messages
            .filter { !it.isRead && it.senderId != userId }
            .map { it.id }
        if (unreadIds.isNotEmpty()) {
            messageListViewModel.markAsRead(unreadIds)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        ChatDetailHeader(
            chat = chat,
            onBackClick = onBackClick
        )
        
        Divider()
        
        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = messageListState.messages,
                key = { it.id }
            ) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == userId,
                    onDelete = { messageListViewModel.deleteMessage(message.id) }
                )
            }
            
            // Typing indicator
            if (typingState.typingUsers.isNotEmpty()) {
                item {
                    TypingIndicator(
                        typingUsers = typingState.typingUsers.toList()
                    )
                }
            }
            
            // Load more button
            if (messageListState.hasMore && !messageListState.isLoading) {
                item {
                    Button(
                        onClick = {
                            messageListViewModel.loadMessages(
                                chat.id,
                                messageListState.currentPage + 1
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Load Earlier Messages")
                    }
                }
            }
            
            // Loading state
            if (messageListState.isLoading && messageListState.messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // Message input
        Divider()
        
        MessageInputBox(
            messageText = messageText,
            onMessageChange = {
                messageText = it
                // Send typing indicator
                scope.launch {
                    messageListViewModel.setTypingIndicator(
                        chat.id,
                        userId,
                        it.isNotEmpty()
                    )
                }
            },
            onSendClick = {
                if (messageText.trim().isNotEmpty()) {
                    val request = SendMessageRequest(
                        chatId = chat.id,
                        senderId = userId,
                        senderName = userName,
                        text = messageText.trim()
                    )
                    sendMessageViewModel.sendMessage(chat.id, request)
                    messageText = ""
                    
                    // Send not typing indicator
                    messageListViewModel.setTypingIndicator(chat.id, userId, false)
                }
            },
            isLoading = sendMessageState.isLoading
        )
    }
}

@Composable
fun ChatDetailHeader(
    chat: Chat,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    chat.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    "${chat.members.size} members",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        IconButton(onClick = { /* TODO: Show options menu */ }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More options"
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDelete: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (isCurrentUser)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        ) {
            if (!isCurrentUser) {
                Text(
                    message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = if (isCurrentUser)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrentUser)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = if (!isCurrentUser) 4.dp else 0.dp)
            )
            
            Text(
                formatMessageTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = if (isCurrentUser)
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator(typingUsers: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            "${typingUsers.joinToString(", ")} is typing...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
fun MessageInputBox(
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            value = messageText,
            onValueChange = onMessageChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp, max = 120.dp),
            placeholder = { Text("Type a message...") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.medium,
            maxLines = 5
        )
        
        Button(
            onClick = onSendClick,
            enabled = messageText.trim().isNotEmpty() && !isLoading,
            modifier = Modifier.height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Send")
            }
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
}
