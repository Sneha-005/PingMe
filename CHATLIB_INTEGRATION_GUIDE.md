# ChatLib - Complete Integration Guide

## Overview

ChatLib is a production-ready Android chat library built with Jetpack Compose that provides a complete chat solution with REST API integration and WebSocket support for real-time messaging. This guide outlines everything that needs to be implemented to make ChatLib a standalone, ready-to-use library.

---

## Current Status

### ✅ Already Implemented

1. **UI Components**
   - ChatScreen, ChatList, ChatInput
   - ChatBubble, ChatAvatar, ChatHeader
   - MessageStatus, TypingIndicator
   - DateSeparator, QuickReplies
   - Fully customizable with theme support

2. **Data Models**
   - Chat, Message, ChatMember
   - ChatMessage, ChatSender
   - Support for different message types (TEXT, IMAGE, VIDEO, AUDIO, FILE)
   - Support for different chat types (ONE_ON_ONE, GROUP, CHANNEL)

3. **Network Layer**
   - REST API service with Retrofit
   - ChatRepository with comprehensive data operations
   - API Response models

4. **Dependency Injection**
   - Hilt setup with ChatNetworkModule
   - ViewModel support

5. **WebSocket Foundation** (in app module)
   - Basic WebSocket connection using OkHttp
   - Message handling

---

## What Needs to Be Implemented

### 1. **WebSocket Service in ChatLib** (HIGH PRIORITY)

#### Files to Create:
- `chatlib/src/main/java/com/devsneha/chatlib/network/WebSocketManager.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/model/WebSocketEvent.kt`

#### Requirements:

```
WebSocketManager should handle:
├── Connection Management
│   ├── Connect to WebSocket server
│   ├── Automatic reconnection with exponential backoff
│   ├── Disconnect gracefully
│   └── Connection state tracking
│
├── Message Operations
│   ├── Send individual messages
│   ├── Send group messages
│   ├── Receive messages in real-time
│   └── Queue messages when offline
│
├── Real-time Events
│   ├── Typing indicators (send/receive)
│   ├── Online/Offline status
│   ├── Message read receipts
│   ├── Delivery confirmation
│   └── User presence updates
│
├── Error Handling
│   ├── Network error recovery
│   ├── Connection timeout handling
│   ├── Message send failures
│   └── Exception logging
│
└── Data Synchronization
    ├── Sync messages when reconnected
    ├── Handle message ordering
    └── Prevent duplicate messages
```

**Implementation Details:**
- Use OkHttp WebSocket (already in dependencies)
- Implement StateFlow for reactive updates
- Support for STOMP protocol (optional but recommended)
- Handle message queueing for offline support
- Use Coroutines for async operations

---

### 2. **Local Database Layer** (MEDIUM PRIORITY)

#### Files to Create:
- `chatlib/src/main/java/com/devsneha/chatlib/db/ChatDatabase.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/db/dao/ChatDao.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/db/dao/MessageDao.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/db/entity/ChatEntity.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/db/entity/MessageEntity.kt`

#### Requirements:
- Use Room database for local caching
- Store chats and messages locally
- Enable offline message viewing
- Support message search functionality
- Auto-sync with server when online

**Dependencies to Add:**
```gradle
implementation "androidx.room:room-runtime:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
```

---

### 3. **Enhanced ChatRepository** (MEDIUM PRIORITY)

#### Modifications to Existing File:
`chatlib/src/main/java/com/devsneha/chatlib/repository/ChatRepository.kt`

**Add:**
```
├── Database integration
├── Offline mode handling
├── Message caching strategies
├── Pagination improvements
├── Search functionality
├── Transaction management
└── Conflict resolution (for duplicate messages)
```

---

### 4. **WebSocket Integration in ChatViewModel** (MEDIUM PRIORITY)

#### Modifications to Existing File:
`chatlib/src/main/java/com/devsneha/chatlib/viewmodel/ChatViewModel.kt`

**Add:**
```
├── WebSocket connection state management
├── Real-time message streaming
├── Typing indicator state management
├── Message status tracking
├── Auto-reconnection logic
├── Message retry mechanism
└── Presence tracking
```

---

### 5. **Configuration & Initialization** (HIGH PRIORITY)

#### Files to Create:
- `chatlib/src/main/java/com/devsneha/chatlib/ChatLibConfig.kt`
- `chatlib/src/main/java/com/devsneha/chatlib/ChatLibInitializer.kt`

#### Requirements:
```
ChatLibConfig should contain:
├── API Base URL
├── WebSocket URL
├── Authentication Token management
├── User ID (current user)
├── Connection timeout values
├── Retry policies
├── Database encryption key
├── Theme configuration
└── Feature flags
```

**Example Configuration:**
```kotlin
ChatLibConfig(
    apiBaseUrl = "https://api.yourserver.com/",
    webSocketUrl = "wss://api.yourserver.com/ws",
    userId = "user_123",
    authToken = "jwt_token_here",
    enableOfflineMode = true,
    enableMessageEncryption = true,
    autoReconnect = true,
    reconnectDelayMs = 1000,
    maxReconnectAttempts = 5
)
```

---

### 6. **Message Encryption** (OPTIONAL - RECOMMENDED)

#### Files to Create:
- `chatlib/src/main/java/com/devsneha/chatlib/security/MessageEncryption.kt`

**Features:**
- End-to-end message encryption (optional)
- Message signing for verification
- Token refresh mechanism

---

### 7. **Notification Support** (RECOMMENDED)

#### Files to Create:
- `chatlib/src/main/java/com/devsneha/chatlib/notification/NotificationManager.kt`

**Features:**
- Local notifications for new messages
- Notification channels configuration
- Badge count support
- Sound and vibration settings

---

### 8. **Testing Suite** (RECOMMENDED)

#### Files to Create:
- `chatlib/src/test/java/com/devsneha/chatlib/`
  - ChatRepositoryTest.kt
  - WebSocketManagerTest.kt
  - ChatViewModelTest.kt

---

### 9. **Documentation** (HIGH PRIORITY)

#### Files to Create:
- Enhanced `chatlib/README.md`
- `chatlib/INTEGRATION_GUIDE.md`
- `chatlib/API_REFERENCE.md`
- `chatlib/EXAMPLES.md`

---

## Backend Requirements

The backend should provide the following to fully support ChatLib:

### 1. **REST API Endpoints**

#### Authentication
```
POST /api/v1/auth/login
POST /api/v1/auth/refresh-token
POST /api/v1/auth/logout
```

#### Chat Management
```
GET    /api/v1/chats                      - List user chats with pagination
GET    /api/v1/chats/{chatId}             - Get chat details
POST   /api/v1/chats                      - Create new chat
PATCH  /api/v1/chats/{chatId}             - Update chat (name, avatar, etc.)
DELETE /api/v1/chats/{chatId}             - Delete/archive chat

GET    /api/v1/chats/{chatId}/messages    - Get messages with pagination
POST   /api/v1/chats/{chatId}/messages    - Send message
PATCH  /api/v1/messages/{messageId}       - Update message
DELETE /api/v1/messages/{messageId}       - Delete message
GET    /api/v1/messages/{messageId}       - Get single message

POST   /api/v1/chats/{chatId}/read        - Mark messages as read
GET    /api/v1/chats/{chatId}/unread      - Get unread count
```

#### User Management
```
GET    /api/v1/users/{userId}             - Get user profile
GET    /api/v1/users/search?q={query}     - Search users
PUT    /api/v1/users/{userId}             - Update user profile
GET    /api/v1/users/{userId}/status      - Get user online status
```

#### Group Operations
```
POST   /api/v1/chats/{chatId}/members     - Add member to group
DELETE /api/v1/chats/{chatId}/members/{userId} - Remove member
PUT    /api/v1/chats/{chatId}/members/{userId} - Update member role
```

### 2. **WebSocket Endpoints & Events**

#### Connection
```
ws://your-server.com/ws?token={jwt_token}&userId={userId}
```

#### Client → Server Events
```
{
  "action": "SEND_MESSAGE",
  "chatId": "chat_123",
  "content": "Hello",
  "type": "TEXT",
  "timestamp": 1234567890
}

{
  "action": "TYPING",
  "chatId": "chat_123",
  "isTyping": true
}

{
  "action": "READ_RECEIPT",
  "chatId": "chat_123",
  "messageId": "msg_123",
  "timestamp": 1234567890
}

{
  "action": "USER_STATUS",
  "status": "ONLINE|AWAY|OFFLINE"
}

{
  "action": "PING"  // Keep-alive
}
```

#### Server → Client Events
```
{
  "action": "MESSAGE_RECEIVED",
  "message": { Message object }
}

{
  "action": "USER_TYPING",
  "chatId": "chat_123",
  "userId": "user_456",
  "isTyping": true
}

{
  "action": "MESSAGE_READ",
  "messageId": "msg_123",
  "readBy": "user_456",
  "timestamp": 1234567890
}

{
  "action": "USER_STATUS_CHANGED",
  "userId": "user_456",
  "status": "ONLINE|AWAY|OFFLINE",
  "timestamp": 1234567890
}

{
  "action": "MESSAGE_DELIVERED",
  "messageId": "msg_123",
  "timestamp": 1234567890
}

{
  "action": "ERROR",
  "error": "error_code",
  "message": "Error description"
}
```

### 3. **Data Models Expected from Backend**

```json
{
  "chat": {
    "id": "string",
    "name": "string",
    "description": "string",
    "avatar": "url",
    "type": "ONE_ON_ONE|GROUP|CHANNEL",
    "members": [
      {
        "id": "string",
        "name": "string",
        "avatar": "url",
        "role": "admin|member",
        "joinedAt": "timestamp"
      }
    ],
    "lastMessage": "string",
    "lastMessageTime": "timestamp",
    "unreadCount": "integer",
    "createdAt": "timestamp",
    "updatedAt": "timestamp",
    "isActive": "boolean"
  },
  
  "message": {
    "id": "string",
    "chatId": "string",
    "senderId": "string",
    "senderName": "string",
    "senderAvatar": "url",
    "text": "string",
    "type": "TEXT|IMAGE|VIDEO|AUDIO|FILE",
    "timestamp": "timestamp",
    "isRead": "boolean",
    "readAt": "timestamp",
    "status": "SENDING|SENT|DELIVERED|READ|FAILED",
    "attachments": [
      {
        "id": "string",
        "url": "url",
        "type": "image|video|audio|file",
        "mimeType": "string"
      }
    ],
    "replyTo": "messageId (optional)"
  },

  "user": {
    "id": "string",
    "name": "string",
    "phone": "string",
    "avatar": "url",
    "status": "ONLINE|AWAY|OFFLINE",
    "lastSeen": "timestamp",
    "bio": "string",
    "createdAt": "timestamp"
  }
}
```

### 4. **Error Handling**

Backend should return standardized error responses:

```json
{
  "success": false,
  "error": "ERROR_CODE",
  "message": "Human readable error message",
  "statusCode": 400
}
```

**Common Error Codes:**
```
UNAUTHORIZED - JWT token invalid/expired
FORBIDDEN - User doesn't have access
NOT_FOUND - Resource not found
VALIDATION_ERROR - Invalid input
INTERNAL_ERROR - Server error
RATE_LIMITED - Too many requests
```

---

## How Users Can Integrate ChatLib

### Step 1: Add Dependency

In your app's `build.gradle.kts`:

```gradle
dependencies {
    // ChatLib dependency
    implementation(project(":chatlib"))
    
    // Or if published to Maven Central
    // implementation("com.devsneha:chatlib:1.0.0")
}
```

### Step 2: Initialize ChatLib

In your Application class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ChatLib
        ChatLib.initialize(this, ChatLibConfig(
            apiBaseUrl = "https://your-api.com/",
            webSocketUrl = "wss://your-api.com/ws",
            userId = getCurrentUserId(),
            authToken = getAuthToken(),
            enableOfflineMode = true
        ))
    }
}
```

### Step 3: Setup Hilt

Add Hilt to your project (if not already done):

```gradle
plugins {
    id("com.google.dagger.hilt.android")
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
}
```

### Step 4: Create Chat Screen

```kotlin
@Composable
fun ChatScreen(navController: NavController) {
    val viewModel: ChatViewModel = hiltViewModel()
    
    ChatScreen(
        messages = viewModel.messages.collectAsState().value,
        recipient = viewModel.currentChat.collectAsState().value,
        inputValue = viewModel.inputText.collectAsState().value,
        onInputChange = { viewModel.updateInputText(it) },
        onSend = { viewModel.sendMessage(it) },
        isTyping = viewModel.isTyping.collectAsState().value,
        showHeader = true,
        showAvatar = true
    )
}
```

### Step 5: Handle Navigation

Use NavController to navigate to chat:

```kotlin
navController.navigate(
    "chat_screen/{chatId}/{recipientId}",
    arguments = listOf(
        navArgument("chatId") { type = NavType.StringType },
        navArgument("recipientId") { type = NavType.StringType }
    )
)
```

### Step 6: Request Permissions (if needed)

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

---

## Implementation Priority

### Phase 1 (Critical - Must Have)
1. WebSocket Service in ChatLib
2. ChatLib Configuration & Initializer
3. Update ChatRepository with WebSocket integration
4. Update ChatViewModel with real-time support
5. Enhanced error handling

**Estimated Time:** 2-3 weeks

### Phase 2 (Important - Should Have)
1. Room Database for local caching
2. Enhanced ChatRepository with database
3. Message encryption (optional)
4. Comprehensive testing suite
5. Notification support

**Estimated Time:** 2-3 weeks

### Phase 3 (Nice to Have)
1. Message search functionality
2. Advanced filtering and sorting
3. Message drafts
4. Voice/video call integration
5. File sharing enhancements

**Estimated Time:** 2-4 weeks

---

## Code Structure After Implementation

```
chatlib/
├── src/main/java/com/devsneha/chatlib/
│   ├── ChatLib.kt                          // Main entry point
│   ├── ChatLibConfig.kt                    // Configuration
│   ├── ChatLibInitializer.kt               // Initialization
│   │
│   ├── di/
│   │   ├── ChatModule.kt                   // Existing
│   │   ├── WebSocketModule.kt              // New
│   │   └── DatabaseModule.kt               // New
│   │
│   ├── network/
│   │   ├── ChatApiService.kt               // Existing
│   │   ├── WebSocketManager.kt             // New
│   │   └── WebSocketInterceptor.kt         // New
│   │
│   ├── db/                                 // New
│   │   ├── ChatDatabase.kt
│   │   ├── dao/
│   │   │   ├── ChatDao.kt
│   │   │   └── MessageDao.kt
│   │   └── entity/
│   │       ├── ChatEntity.kt
│   │       └── MessageEntity.kt
│   │
│   ├── model/
│   │   ├── ApiResponse.kt                  // Existing
│   │   ├── Chat.kt                         // Existing
│   │   ├── Message.kt                      // Existing
│   │   ├── WebSocketEvent.kt               // New
│   │   └── WebSocketMessage.kt             // New
│   │
│   ├── repository/
│   │   └── ChatRepository.kt               // Enhanced
│   │
│   ├── viewmodel/
│   │   └── ChatViewModel.kt                // Enhanced
│   │
│   ├── notification/                       // New
│   │   └── NotificationManager.kt
│   │
│   ├── security/                           // New
│   │   └── MessageEncryption.kt
│   │
│   ├── ui/
│   │   ├── avatar/                         // Existing
│   │   ├── bubble/                         // Existing
│   │   ├── header/                         // Existing
│   │   ├── input/                          // Existing
│   │   ├── list/                           // Existing
│   │   ├── message/                        // Existing
│   │   ├── replies/                        // Existing
│   │   ├── screen/                         // Existing
│   │   ├── separator/                      // Existing
│   │   ├── status/                         // Existing
│   │   ├── theme/                          // Existing
│   │   └── typing/                         // Existing
│   │
│   ├── util/
│   │   ├── Constants.kt                    // Existing
│   │   ├── DateFormatter.kt                // Existing
│   │   └── WebSocketConstants.kt           // New
│   │
│   └── theme/                              // Existing
│       ├── ChatColors.kt
│       ├── ChatShapes.kt
│       └── ChatTypography.kt
│
├── src/test/java/com/devsneha/chatlib/    // New
│   ├── ChatRepositoryTest.kt
│   ├── WebSocketManagerTest.kt
│   ├── ChatViewModelTest.kt
│   └── ...
│
├── README.md                               // Enhanced
├── INTEGRATION_GUIDE.md                    // New
├── API_REFERENCE.md                        // New
└── EXAMPLES.md                             // New
```

---

## Key Design Decisions to Make

1. **WebSocket Protocol:** Raw WebSocket vs STOMP?
2. **Message Ordering:** Server timestamp vs client timestamp?
3. **Offline Support:** Queue with persistence or discard messages?
4. **Encryption:** End-to-end (E2E) or in-transit only?
5. **Notifications:** Local only or push notifications?
6. **Database:** SQLite with Room or SharedPreferences?

---

## Performance Considerations

- Implement pagination for large message lists
- Use Flow-based reactive updates
- Optimize UI recomposition with proper key usage
- Implement message indexing in database
- Use proper coroutine scope management
- Implement connection pooling for API calls

---

## Security Considerations

- Validate all inputs before sending to server
- Store auth tokens securely (EncryptedSharedPreferences)
- Implement certificate pinning for HTTPS
- Use secure WebSocket (WSS) protocol
- Implement request signing if needed
- Regular token refresh mechanism

---

## Next Steps

1. **Review this document** with your team
2. **Prioritize features** based on your needs
3. **Start Phase 1 implementation**
4. **Setup backend endpoints** in parallel
5. **Create integration examples** for users
6. **Setup CI/CD pipeline** for library publishing
7. **Write comprehensive tests** at each phase

---

## Questions to Ask Backend Team

1. What's the exact WebSocket message format?
2. Do you support STOMP protocol?
3. What's the auth mechanism (JWT, OAuth)?
4. What's the rate limiting policy?
5. Do you support message search?
6. How long are messages retained?
7. Do you support message encryption?
8. What's the file size limit for attachments?
9. Do you have sample API responses?
10. What's the SLA for message delivery?

---

## Success Criteria

✅ ChatLib should be:
- Usable as a standalone library
- Easy to integrate (< 5 steps)
- Well-documented with examples
- Production-ready with error handling
- Customizable (colors, fonts, icons)
- Performant (handles 1000+ messages)
- Offline-capable
- Secure (encryption, token management)
- Tested (unit & integration tests)
- Published to Maven Central (optional)

