# PingMe - WhatsApp-like Chat App

A modern Android chat application built with Jetpack Compose, featuring real-time messaging, user authentication, and a beautiful Material Design 3 UI.

## Features

### 🔐 Authentication
- User registration with username and phone number
- OTP verification for secure account creation
- Form validation and error handling

### 💬 Chat Functionality
- **Individual Messages**: Send messages to specific users
- **Group Messages**: Send messages to group conversations
- **Conversation Management**: View all conversations with pagination
- **Real-time Messaging**: Send and receive messages instantly via WebSocket
- **Message Status**: See message delivery and read status
- **Typing Indicators**: Know when someone is typing
- **Search**: Find users and conversations quickly
- **New Chat Creation**: Start conversations with other users
- **Group Management**: Create groups, add/remove participants

### 🎨 Modern UI
- Material Design 3 with dynamic theming
- WhatsApp-like chat bubbles
- Profile pictures with Coil image loading
- Responsive design for different screen sizes
- Smooth animations and transitions

### 🔧 Technical Features
- **MVVM Architecture**: Clean separation of concerns
- **Jetpack Compose**: Modern declarative UI
- **Hilt Dependency Injection**: Efficient dependency management
- **Retrofit & OkHttp**: Robust network communication
- **WebSocket**: Real-time messaging with STOMP protocol
- **Coroutines & Flow**: Asynchronous programming
- **Navigation Compose**: Type-safe navigation

## Architecture

```
app/
├── data/
│   ├── remote/
│   │   ├── ApiService.kt          # REST API endpoints
│   │   └── WebSocketService.kt    # Real-time messaging
│   └── repository/
│       ├── ApiServiceRepository.kt # Auth repository
│       └── ChatRepository.kt      # Chat repository
├── di/
│   └── NetworkModule.kt           # Dependency injection
├── model/
│   ├── ChatModels.kt              # Chat data models
│   └── AuthModels.kt              # Authentication models
├── presentation/
│   ├── auth/                      # Authentication screens
│   │   ├── RegisterScreen.kt
│   │   ├── OtpVerifyScreen.kt
│   │   └── ViewModels
│   └── chat/                      # Chat screens
│       ├── ChatListScreen.kt
│       ├── SimpleChatScreen.kt
│       ├── NewChatScreen.kt
│       └── ViewModels
└── utility/                       # Validation utilities
```

## API Endpoints

### Authentication
- `POST /api/users/send-otp` - Send OTP to phone number
- `POST /api/users/verify-otp` - Verify OTP and create account

### Individual Messages
- `POST /api/chat/send` - Send message to another user
- `GET /api/chat/messages/{user1Id}/{user2Id}` - Get messages between users

### Group Messages
- `POST /api/chat/group/send` - Send message to group conversation
- `GET /api/chat/conversation/{conversationId}/messages` - Get conversation messages

### Conversations
- `GET /api/chat/conversations/{userId}` - Get user's conversations
- `POST /api/chat/group/create` - Create new group conversation
- `POST /api/chat/group/{conversationId}/add-participant` - Add participant to group
- `DELETE /api/chat/group/{conversationId}/remove-participant` - Remove participant from group

### Message Status
- `POST /api/chat/read` - Mark messages as read
- `GET /api/chat/unread-count/{userId}` - Get unread message count

### Typing and Status
- `POST /api/chat/typing` - Send typing indicator
- `POST /api/chat/status` - Send online/offline status

### User Management
- `GET /api/users/search` - Search users
- `GET /api/users/{userId}` - Get user profile
- `PUT /api/users/profile` - Update user profile

## WebSocket Integration

### Connection
Connect to WebSocket at `ws://localhost:8080/ws`

### Message Destinations
- `/app/chat.sendMessage` - Send individual/group message
- `/app/chat.addUser` - Add user to chat (online notification)
- `/app/chat.typing` - Send typing indicator
- `/app/chat.read` - Mark messages as read
- `/app/chat.leave` - Leave chat (offline notification)

### Subscriptions
- `/user/{userId}/queue/messages` - Receive personal messages
- `/user/{userId}/queue/typing` - Receive typing indicators
- `/topic/status` - Receive status updates
- `/topic/public` - Receive public messages

## Data Models

### User
```kotlin
data class User(
    val id: Long,
    val username: String,
    val phoneNumber: String?,
    val profilePicture: String?,
    val status: String?,
    val isOnline: Boolean,
    val lastSeen: Date?
)
```

### Conversation
```kotlin
data class Conversation(
    val id: Long,
    val name: String,
    val type: ConversationType, // INDIVIDUAL or GROUP
    val description: String?,
    val createdBy: User,
    val createdAt: Date,
    val lastMessageAt: Date,
    val participants: List<User>
)
```

### Message
```kotlin
data class Message(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long?,
    val receiverName: String?,
    val content: String,
    val messageType: MessageType, // TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, LOCATION
    val timestamp: Date,
    val isRead: Boolean,
    val isDelivered: Boolean,
    val conversationId: Long?,
    val conversationName: String?,
    val action: String?
)
```

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd PingMe
   ```

2. **Configure API Base URL**
   Update the base URL in `NetworkModule.kt`:
   ```kotlin
   private const val BASE_URL = "https://your-api-domain.com/"
   ```

3. **Configure WebSocket URL**
   Update the WebSocket URL in your ViewModels:
   ```kotlin
   private const val WEBSOCKET_URL = "ws://your-websocket-domain.com/ws"
   ```

4. **Set Current User ID**
   Update the current user ID in ViewModels (currently hardcoded as 1L):
   ```kotlin
   private val currentUserId: Long = 1L // Get from user session
   ```

5. **Build and Run**
   ```bash
   ./gradlew build
   ```

## Dependencies

### Core
- **Jetpack Compose**: Modern UI toolkit
- **Material Design 3**: Latest design system
- **Navigation Compose**: Type-safe navigation
- **Hilt**: Dependency injection

### Networking
- **Retrofit**: HTTP client
- **OkHttp**: HTTP client implementation
- **WebSocket**: Real-time communication
- **Gson**: JSON serialization

### Image Loading
- **Coil**: Image loading for Compose

### Async Programming
- **Coroutines**: Asynchronous programming
- **Flow**: Reactive streams

## Key Features Implementation

### Individual vs Group Messages
The app supports both individual and group messaging:

```kotlin
// Individual message
chatRepository.sendMessage(senderId, receiverId, content)

// Group message
chatRepository.sendGroupMessage(senderId, conversationId, content)
```

### Real-time Messaging
WebSocket integration for instant message delivery:

```kotlin
// Connect to WebSocket
webSocketService.connect(WEBSOCKET_URL, userId)

// Send message via WebSocket
webSocketService.sendMessage(senderId, receiverId, content)

// Receive messages
webSocketService.newMessages.receive()
```

### Conversation Management
Support for both individual and group conversations:

```kotlin
// Get user conversations
chatRepository.getUserConversations(userId, page, size)

// Create group conversation
chatRepository.createGroupConversation(name, description, creatorId, participantIds)
```

### Message Status
Messages show delivery and read status:

```kotlin
// Mark messages as read
chatRepository.markMessagesAsRead(userId, senderId)

// Get unread count
chatRepository.getUnreadMessageCount(userId)
```

### Typing Indicators
Real-time typing status:

```kotlin
// Send typing indicator
chatRepository.sendTypingIndicator(senderId, receiverId, isTyping)

// WebSocket typing indicator
webSocketService.sendTypingIndicator(senderId, receiverId, isTyping)
```

## Navigation Flow

1. **Registration** → **OTP Verification** → **Chat List**
2. **Chat List** → **Individual Chat** (`/user/{userId}`) or **Group Chat** (`/conversation/{conversationId}`)
3. **New Chat** → **User Selection** → **Individual Chat**

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Future Enhancements

- [ ] Media sharing (images, videos, documents)
- [ ] Voice messages
- [ ] Message reactions
- [ ] Message replies
- [ ] Push notifications
- [ ] Message encryption
- [ ] Dark mode support
- [ ] Multi-language support
- [ ] Offline message sync
- [ ] Message search
- [ ] Message forwarding
- [ ] Message deletion
- [ ] User blocking
- [ ] Message pinning 