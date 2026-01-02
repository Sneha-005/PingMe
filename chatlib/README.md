# ChatLib - Android Chat UI Library

A comprehensive, customizable chat UI library for Android built with Jetpack Compose. This library provides all the essential components needed to build a modern chat interface.

## Features

- 🎨 **Modern UI Components** - Beautiful, Material Design 3 based components
- 💬 **Message Types** - Support for text, image, video, audio, and file messages
- 👤 **Avatar Support** - User avatars with image loading and fallback initials
- 📅 **Date Separators** - Automatic date/time separators between messages
- ✅ **Message Status** - Sent, delivered, and read status indicators
- ⌨️ **Typing Indicator** - Animated typing indicator
- 🎯 **Quick Replies** - Quick reply buttons for faster responses
- 🎨 **Customizable Theme** - Easy theming with colors, shapes, and typography
- 📱 **Responsive** - Works on phones and tablets

## Components

### Core Components

#### ChatScreen
The main chat screen component that combines all other components.

```kotlin
ChatScreen(
    messages = messages,
    recipient = recipient,
    inputValue = inputValue,
    onInputChange = { inputValue = it },
    onSend = { /* send message */ },
    onQuickReply = { reply -> /* handle quick reply */ },
    isTyping = isTyping,
    showHeader = true,
    showAvatar = true
)
```

#### ChatList
Displays a list of messages with automatic scrolling and date separators.

```kotlin
ChatList(
    messages = messages,
    showAvatar = true,
    showTimestamp = true,
    showStatus = true,
    showDateSeparators = true,
    autoScroll = true
)
```

#### ChatMessageItem
Individual message item with support for different message types.

```kotlin
ChatMessageItem(
    message = message,
    showAvatar = true,
    showTimestamp = true,
    showStatus = true,
    status = MessageStatus.READ
)
```

#### ChatInput
Input field for typing and sending messages.

```kotlin
ChatInput(
    value = inputValue,
    onValueChange = { inputValue = it },
    onSend = { /* send */ },
    onAttachmentClick = { /* attach file */ },
    placeholder = "Type a message..."
)
```

### Supporting Components

- **ChatAvatar** - User avatar with image loading support
- **ChatHeader** - Chat screen header with recipient info
- **DateSeparator** - Date/time separators between messages
- **MessageStatusIcon** - Status indicators (sent, delivered, read)
- **TypingIndicator** - Animated typing indicator
- **QuickReplies** - Quick reply buttons
- **ChatBubble** - Message bubble component

## Models

### ChatMessage
```kotlin
data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val content: String,
    val type: ChatMessageType = ChatMessageType.TEXT,
    val timestamp: Long,
    val isMine: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val replyTo: String? = null
)
```

### ChatSender
```kotlin
data class ChatSender(
    val id: String,
    val name: String,
    val avatarUrl: String? = null
)
```

### Message Types
- `TEXT` - Text messages
- `IMAGE` - Image messages
- `VIDEO` - Video messages
- `AUDIO` - Audio messages
- `FILE` - File attachments

### Message Status
- `SENDING` - Message is being sent
- `SENT` - Message sent successfully
- `DELIVERED` - Message delivered to recipient
- `READ` - Message read by recipient

## Theme

The library provides a comprehensive theming system:

### ChatColors
```kotlin
ChatColors.sentMessageBackground
ChatColors.receivedMessageBackground
ChatColors.messageStatusRead
// ... and more
```

### ChatShapes
```kotlin
ChatShapes.messageBubble
ChatShapes.sentMessageBubble
ChatShapes.receivedMessageBubble
// ... and more
```

### ChatTypography
```kotlin
ChatTypography.messageText
ChatTypography.messageTimestamp
ChatTypography.senderName
// ... and more
```

## Utilities

### DateFormatter
Format timestamps for display:
```kotlin
DateFormatter.formatMessageTime(timestamp)
DateFormatter.formatSeparatorDate(timestamp)
DateFormatter.formatTimeOnly(timestamp)
```

## Usage Example

```kotlin
@Composable
fun MyChatScreen() {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputValue by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    
    val recipient = ChatSender(
        id = "1",
        name = "John Doe",
        avatarUrl = "https://example.com/avatar.jpg"
    )
    
    ChatScreen(
        messages = messages,
        recipient = recipient,
        inputValue = inputValue,
        onInputChange = { inputValue = it },
        onSend = {
            if (inputValue.isNotBlank()) {
                val newMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = currentUser,
                    content = inputValue,
                    timestamp = System.currentTimeMillis(),
                    isMine = true,
                    status = MessageStatus.SENDING
                )
                messages = messages + newMessage
                inputValue = ""
                // Send message to server
            }
        },
        isTyping = isTyping,
        showHeader = true,
        showAvatar = true
    )
}
```

## Dependencies

The library uses:
- Jetpack Compose
- Material Design 3
- Coil for image loading
- Kotlin Coroutines

## Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":chatlib"))
}
```

## License

This library is available for use in your projects.

