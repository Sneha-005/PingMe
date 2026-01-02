# ChatLib - User Integration Guide

A complete step-by-step guide for developers to integrate ChatLib into their Android applications.

---

## Table of Contents

1. [Quick Start (5 Minutes)](#quick-start)
2. [Installation](#installation)
3. [Configuration](#configuration)
4. [Basic Implementation](#basic-implementation)
5. [Advanced Features](#advanced-features)
6. [Customization](#customization)
7. [Troubleshooting](#troubleshooting)
8. [Complete Examples](#complete-examples)

---

## Quick Start

### Step 1: Add Dependency

In your app's `build.gradle.kts`:

```gradle
dependencies {
    // ChatLib dependency
    implementation(project(":chatlib"))
    
    // If using Maven Central (future):
    // implementation("com.devsneha:chatlib:1.0.0")
}
```

### Step 2: Configure in Application Class

```kotlin
import android.app.Application
import com.devsneha.chatlib.ChatLib
import com.devsneha.chatlib.ChatLibConfig

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ChatLib with your configuration
        ChatLib.initialize(
            context = this,
            config = ChatLibConfig(
                apiBaseUrl = "https://your-api.com/",
                webSocketUrl = "wss://your-api.com/ws",
                userId = "current_user_id",
                authToken = "your_jwt_token",
                enableOfflineMode = true,
                autoReconnect = true
            )
        )
    }
}
```

### Step 3: Create Chat Screen

```kotlin
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.devsneha.chatlib.ui.screen.ChatScreen
import com.devsneha.chatlib.viewmodel.ChatViewModel

@Composable
fun MyChatScreen() {
    val viewModel: ChatViewModel = hiltViewModel()
    
    ChatScreen(
        viewModel = viewModel,
        onBackClick = { /* handle back */ }
    )
}
```

### Step 4: Setup Navigation

```kotlin
// In your navigation graph
composable(
    route = "chat/{chatId}",
    arguments = listOf(
        navArgument("chatId") { type = NavType.StringType }
    )
) { backStackEntry ->
    MyChatScreen()
}
```

**Done! You now have a working chat feature.** 🎉

---

## Installation

### Prerequisites

- Android 7.0 (API 24) or higher
- Android Studio Flamingo or later
- Kotlin 1.9+
- Jetpack Compose 1.6+

### Add ChatLib to Your Project

#### Option 1: Local Module (Development)

```gradle
// In settings.gradle.kts
include(":chatlib")
project(":chatlib").projectDir = File("../PingMe/chatlib")

// In app/build.gradle.kts
dependencies {
    implementation(project(":chatlib"))
}
```

#### Option 2: Maven Central (When Published)

```gradle
dependencies {
    implementation("com.devsneha:chatlib:1.0.0")
}
```

#### Option 3: GitHub Packages

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/devsneha/PingMe")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.devsneha:chatlib:1.0.0")
}
```

### Required Dependencies (Auto-Included)

ChatLib includes these dependencies, so you don't need to add them separately:

```gradle
// Jetpack Compose
androidx.compose.*

// Jetpack & Architecture
androidx.lifecycle.*
androidx.hilt.*
androidx.room.*

// Networking
retrofit2
okhttp3
com.google.code.gson:gson

// DI
com.google.dagger:hilt-android

// Async
kotlinx.coroutines.*
```

---

## Configuration

### ChatLibConfig Parameters

```kotlin
ChatLibConfig(
    // REQUIRED - Backend URLs
    apiBaseUrl: String = "https://api.your-server.com/",
    webSocketUrl: String = "wss://api.your-server.com/ws",
    
    // REQUIRED - User Information
    userId: String,                    // Current user ID
    authToken: String,                 // JWT token for authentication
    
    // OPTIONAL - Connection Settings
    enableOfflineMode: Boolean = true,           // Cache messages locally
    autoReconnect: Boolean = true,               // Auto-reconnect on disconnect
    reconnectDelayMs: Long = 1000,               // Initial reconnect delay
    maxReconnectAttempts: Int = 5,               // Max reconnection attempts
    requestTimeoutMs: Long = 30000,              // Request timeout
    
    // OPTIONAL - Features
    enableMessageEncryption: Boolean = false,    // E2E encryption (beta)
    enableNotifications: Boolean = true,         // Push notifications
    enableVoiceMessages: Boolean = false,        // Voice message support
    enableFileSharing: Boolean = true,           // File/document sharing
    enableGroupCalls: Boolean = false,           // Group video calls
    
    // OPTIONAL - UI Customization
    maxMessageLength: Int = 5000,                // Character limit
    maxFileSize: Long = 100 * 1024 * 1024,      // 100 MB
    typingIndicatorTimeout: Long = 3000,        // Show typing for 3 seconds
    messageLoadPageSize: Int = 50,               // Messages per page
    
    // OPTIONAL - Theme
    primaryColor: Color? = null,                 // Override primary color
    secondaryColor: Color? = null,               // Override secondary color
    
    // OPTIONAL - Logging
    enableDebugLogging: Boolean = BuildConfig.DEBUG,
    logFilePath: String? = null                  // Log to file
)
```

### Example Configurations

#### Minimal Configuration

```kotlin
ChatLib.initialize(
    context = this,
    config = ChatLibConfig(
        apiBaseUrl = "https://api.example.com/",
        webSocketUrl = "wss://api.example.com/ws",
        userId = userId,
        authToken = token
    )
)
```

#### Production Configuration

```kotlin
ChatLib.initialize(
    context = this,
    config = ChatLibConfig(
        apiBaseUrl = "https://api.example.com/",
        webSocketUrl = "wss://api.example.com/ws",
        userId = userId,
        authToken = token,
        enableOfflineMode = true,
        autoReconnect = true,
        maxReconnectAttempts = 10,
        enableMessageEncryption = true,
        enableNotifications = true,
        enableFileSharing = true,
        enableDebugLogging = false
    )
)
```

#### Development Configuration

```kotlin
ChatLib.initialize(
    context = this,
    config = ChatLibConfig(
        apiBaseUrl = "http://localhost:8080/",
        webSocketUrl = "ws://localhost:8080/ws",
        userId = "dev_user_123",
        authToken = "dev_token",
        enableOfflineMode = false,
        autoReconnect = true,
        enableDebugLogging = true,
        logFilePath = "${context.cacheDir}/chatlib.log"
    )
)
```

---

## Basic Implementation

### 1. Setup Hilt in Your App

If not already done, add Hilt to your project:

```gradle
// In build.gradle (root)
plugins {
    id("com.google.dagger.hilt.android") version "2.48" apply false
}

// In app/build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
}
```

### 2. Create Application Class

```kotlin
import android.app.Application
import com.devsneha.chatlib.ChatLib
import com.devsneha.chatlib.ChatLibConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val token = getTokenFromPreferences()
        val userId = getUserIdFromPreferences()
        
        ChatLib.initialize(
            context = this,
            config = ChatLibConfig(
                apiBaseUrl = BuildConfig.API_BASE_URL,
                webSocketUrl = BuildConfig.WEBSOCKET_URL,
                userId = userId,
                authToken = token
            )
        )
    }
    
    private fun getTokenFromPreferences(): String {
        // Get from SharedPreferences or DataStore
        return ""
    }
    
    private fun getUserIdFromPreferences(): String {
        // Get from SharedPreferences or DataStore
        return ""
    }
}
```

Declare in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### 3. Create Chat Screen

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.devsneha.chatlib.ui.screen.ChatScreen
import com.devsneha.chatlib.viewmodel.ChatViewModel

@Composable
fun ChatScreenWrapper(
    chatId: String,
    onBackClick: () -> Unit,
    onCallClick: (String) -> Unit = { }
) {
    val viewModel: ChatViewModel = hiltViewModel()
    
    // Load chat when screen appears
    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }
    
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        ErrorScreen(
            error = error,
            onRetry = { viewModel.retry() },
            onBack = onBackClick
        )
    } else {
        ChatScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onCallClick = onCallClick
        )
    }
}
```

### 4. Setup Navigation

```kotlin
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.Composable

@Composable
fun AppNavGraph(navController: NavController) {
    NavHost(
        navController = navController,
        startDestination = "chat_list"
    ) {
        composable(route = "chat_list") {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate("chat/$chatId")
                }
            )
        }
        
        composable(
            route = "chat/{chatId}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            ChatScreenWrapper(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

### 5. Request Permissions

In `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

Request at runtime for Android 13+:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermission(Manifest.permission.POST_NOTIFICATIONS)
}
```

---

## Advanced Features

### Real-Time Message Updates

```kotlin
@Composable
fun ChatScreenWithRealTime() {
    val viewModel: ChatViewModel = hiltViewModel()
    
    // Observe real-time messages
    val messages = viewModel.messages.collectAsState().value
    val isTyping = viewModel.isTyping.collectAsState().value
    val connectionState = viewModel.connectionState.collectAsState().value
    
    ChatScreen(
        messages = messages,
        isTyping = isTyping,
        showConnectionStatus = true,
        connectionState = connectionState
    )
}
```

### Handling Message Status

```kotlin
// The ChatViewModel automatically manages message status:
// SENDING -> SENT -> DELIVERED -> READ

// You can track individual message status
val messageStatus = viewModel.getMessageStatus(messageId)
// Returns: SENDING, SENT, DELIVERED, READ, FAILED

// Or listen to status changes
LaunchedEffect(Unit) {
    viewModel.messageStatusUpdates.collect { (messageId, status) ->
        Log.d("Chat", "Message $messageId status: $status")
    }
}
```

### Offline Support

```kotlin
// Messages are automatically cached and queued offline
// When connection is restored, messages are automatically sent

// Check if offline
val isOnline = viewModel.connectionState.collectAsState().value == ConnectionState.CONNECTED

// Listen for sync completion
LaunchedEffect(Unit) {
    viewModel.syncEvents.collect { event ->
        when (event) {
            is SyncEvent.SyncStarted -> Log.d("Chat", "Syncing messages...")
            is SyncEvent.SyncCompleted -> Log.d("Chat", "All messages synced")
            is SyncEvent.SyncFailed -> Log.d("Chat", "Sync failed: ${event.error}")
        }
    }
}
```

### Typing Indicators

```kotlin
var isTyping by remember { mutableStateOf(false) }

// Send typing indicator
fun onTextChanged(text: String) {
    if (text.isNotEmpty() && !isTyping) {
        viewModel.sendTypingIndicator(true)
        isTyping = true
    } else if (text.isEmpty() && isTyping) {
        viewModel.sendTypingIndicator(false)
        isTyping = false
    }
}

// Receive typing indicators
val otherUserTyping = viewModel.otherUserTyping.collectAsState().value
if (otherUserTyping) {
    TypingIndicator()
}
```

### Message Search

```kotlin
var searchQuery by remember { mutableStateOf("") }

LaunchedEffect(searchQuery) {
    if (searchQuery.isNotEmpty()) {
        viewModel.searchMessages(searchQuery)
    }
}

val searchResults = viewModel.searchResults.collectAsState().value
```

### Message Reactions (if supported)

```kotlin
// Add reaction to message
viewModel.addReaction(
    messageId = "msg_123",
    emoji = "👍"
)

// Remove reaction
viewModel.removeReaction(
    messageId = "msg_123",
    emoji = "👍"
)

// Get reactions for a message
val reactions = viewModel.getMessageReactions(messageId)
```

---

## Customization

### Theme Customization

```kotlin
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTypography
import androidx.compose.material3.LocalColorScheme
import androidx.compose.material3.LocalShapes
import androidx.compose.material3.LocalTypography

@Composable
fun CustomChatScreen() {
    val customColors = ChatColors(
        primaryColor = Color(0xFF2196F3),
        secondaryColor = Color(0xFF03DAC6),
        messageBackgroundMine = Color(0xFFE3F2FD),
        messageBackgroundTheirs = Color(0xFFF5F5F5),
        messageTextMine = Color.Black,
        messageTextTheirs = Color.Black
    )
    
    val customShapes = ChatShapes(
        messageBubbleShape = RoundedCornerShape(12.dp),
        inputShape = RoundedCornerShape(24.dp)
    )
    
    val customTypography = ChatTypography(
        messageText = TextStyle(fontSize = 14.sp),
        senderName = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
        timestamp = TextStyle(fontSize = 10.sp)
    )
    
    ChatScreen(
        colors = customColors,
        shapes = customShapes,
        typography = customTypography
    )
}
```

### Custom Message Types

```kotlin
// Support for custom message types
enum class CustomMessageType {
    TEXT, IMAGE, VIDEO, LOCATION, CONTACT, STICKER
}

// Send custom message type
viewModel.sendMessage(
    content = locationJson,
    type = ChatMessageType.TEXT, // Store custom type in metadata
    metadata = mapOf("type" to "LOCATION")
)
```

### Custom UI Components

```kotlin
// Replace default message bubble
@Composable
fun CustomMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (message.isMine) Color.Blue else Color.Gray,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            if (!message.isMine) {
                Text(message.sender.name, fontWeight = FontWeight.Bold)
            }
            Text(message.content)
            Text(message.timestamp.toFormattedTime(), fontSize = 10.sp)
        }
    }
}
```

---

## Troubleshooting

### Common Issues

#### 1. WebSocket Connection Fails

**Problem:** Messages aren't being received in real-time

```kotlin
// Check connection state
val state = viewModel.connectionState.collectAsState().value
Log.d("Chat", "Connection state: $state")

// Enable debug logging
ChatLib.initialize(
    context = this,
    config = config.copy(enableDebugLogging = true)
)

// Check logs
adb logcat | grep "ChatLib"
```

**Solutions:**
- Verify WebSocket URL is correct
- Check JWT token is valid and not expired
- Ensure HTTPS/WSS is used in production
- Check firewall/network rules allow WebSocket

#### 2. Messages Slow to Load

**Problem:** Message list takes time to display

```kotlin
// Use pagination efficiently
viewModel.loadMessages(
    chatId = chatId,
    pageSize = 30  // Reduce from default 50
)

// Enable database caching
config = config.copy(enableOfflineMode = true)

// Profile performance
ChatLib.enablePerformanceProfiling()
```

#### 3. Token Expiry

**Problem:** "Unauthorized" errors after some time

```kotlin
// Update token when it expires
viewModel.refreshToken(newToken)

// Or reinitialize ChatLib with new token
ChatLib.updateConfig(
    config.copy(authToken = newToken)
)
```

#### 4. Duplicate Messages

**Problem:** Messages appear twice in the list

```kotlin
// This is usually handled automatically, but you can:
viewModel.deduplicateMessages()

// Or clear and reload
viewModel.clearMessages()
viewModel.loadMessages(chatId)
```

#### 5. High Memory Usage

**Problem:** App uses too much memory with many messages

```kotlin
// Implement message pagination
viewModel.loadMoreMessages(pageSize = 20)

// Or limit in-memory messages
ChatLib.setCacheConfig(
    maxInMemoryMessages = 500
)

// Monitor memory
StrictMode.enableDefaults()
```

---

## Complete Examples

### Example 1: Simple Chat List Screen

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SimpleChatListScreen(
    onChatClick: (String) -> Unit,
    onNewChatClick: () -> Unit
) {
    val viewModel: ChatViewModel = hiltViewModel()
    
    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }
    
    val chats = viewModel.chats.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chats") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNewChatClick) {
                Icon(Icons.Default.Add, "New Chat")
            }
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            chats.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No chats yet")
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(chats) { chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = { onChatClick(chat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(chat.name.first().toString())
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Chat info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = chat.lastMessage ?: "No messages",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Unread badge
            if (chat.unreadCount > 0) {
                Badge {
                    Text(chat.unreadCount.toString())
                }
            }
        }
        
        Divider()
    }
}
```

### Example 2: Full Chat Screen with All Features

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FullChatScreen(
    chatId: String,
    onBackClick: () -> Unit
) {
    val viewModel: ChatViewModel = hiltViewModel()
    
    // Load chat
    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
        viewModel.loadMessages(chatId)
    }
    
    // Observe states
    val messages = viewModel.messages.collectAsState().value
    val inputText by viewModel.inputText.collectAsState()
    val isTyping = viewModel.isTyping.collectAsState().value
    val connectionState = viewModel.connectionState.collectAsState().value
    val chat = viewModel.currentChat.collectAsState().value
    val listState = rememberLazyListState()
    
    var myTypingTimer: Job? = null
    
    LaunchedEffect(Unit) {
        // Scroll to bottom when new messages arrive
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(chat?.name ?: "Chat")
                        if (isTyping) {
                            Text(
                                "typing...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (connectionState == ConnectionState.CONNECTED)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = messages,
                    key = { it.id }
                ) { message ->
                    MessageBubble(
                        message = message,
                        onReaction = { emoji ->
                            viewModel.addReaction(message.id, emoji)
                        }
                    )
                }
                
                // Show typing indicator
                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }
            
            Divider()
            
            // Input field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { newText ->
                        viewModel.updateInputText(newText)
                        
                        // Send typing indicator
                        myTypingTimer?.cancel()
                        viewModel.sendTypingIndicator(true)
                        
                        myTypingTimer = viewModelScope.launch {
                            delay(3000)
                            viewModel.sendTypingIndicator(false)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    placeholder = { Text("Type message...") },
                    maxLines = 3,
                    shape = RoundedCornerShape(24.dp)
                )
                
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            viewModel.updateInputText("")
                        }
                    },
                    enabled = inputText.isNotBlank() && connectionState == ConnectionState.CONNECTED
                ) {
                    Icon(Icons.Default.Send, "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onReaction: (String) -> Unit
) {
    val alignment = if (message.isMine) Alignment.End else Alignment.Start
    
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!message.isMine) {
            Text(
                text = message.sender.name,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Surface(
            color = if (message.isMine)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = if (message.isMine)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp.toFormattedTime(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                    
                    if (message.isMine) {
                        MessageStatus(message.status)
                    }
                }
            }
        }
    }
}
```

---

## Best Practices

✅ **DO:**
- Initialize ChatLib in Application.onCreate()
- Use Hilt for dependency injection
- Handle network state changes gracefully
- Implement offline-first approach
- Use proper error handling and user feedback
- Secure store auth tokens (EncryptedSharedPreferences)
- Test with actual WebSocket server
- Monitor connection quality

❌ **DON'T:**
- Initialize ChatLib multiple times
- Store tokens in plain text
- Block UI thread with long operations
- Use GlobalScope for coroutines
- Hardcode API URLs
- Skip error handling
- Assume always-online scenario
- Load all messages at once

---

## Support & Resources

- **Documentation**: [Full Docs](./README.md)
- **Architecture**: [Architecture Guide](./CHATLIB_ARCHITECTURE.md)
- **Backend Requirements**: [Backend Spec](./BACKEND_REQUIREMENTS.md)
- **GitHub Issues**: [Issue Tracker](https://github.com/devsneha/PingMe/issues)
- **Email Support**: support@devsneha.com

