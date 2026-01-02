# ChatLib Architecture & Implementation Plan

## Current Architecture vs Future Architecture

### CURRENT STATE

```
┌─────────────────────────────────────────────────────────────┐
│                     UI Layer (Jetpack Compose)              │
├─────────────────────────────────────────────────────────────┤
│  ChatScreen │ ChatList │ ChatBubble │ InputField │ Avatar   │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   ViewModel Layer                           │
├─────────────────────────────────────────────────────────────┤
│  ChatViewModel (MutableStateFlow, LiveData)                 │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  Repository Layer                           │
├─────────────────────────────────────────────────────────────┤
│  ChatRepository (REST API only)                             │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┴────────────────┐
        │                                 │
┌───────▼──────────┐        ┌────────────▼──────────┐
│  REST API        │        │  WebSocket (Basic)    │
│  (Retrofit)      │        │  (In App Module Only) │
└──────────────────┘        └───────────────────────┘
```

### FUTURE STATE (After Implementation)

```
┌─────────────────────────────────────────────────────────────┐
│                     UI Layer (Jetpack Compose)              │
├─────────────────────────────────────────────────────────────┤
│  ChatScreen │ ChatList │ ChatBubble │ InputField │ Avatar   │
│  Notifications │ MessageStatus │ TypingIndicator           │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                   ViewModel Layer                           │
├─────────────────────────────────────────────────────────────┤
│  ChatViewModel (StateFlow, Reactive)                        │
│  - Real-time message handling                              │
│  - Typing indicators                                        │
│  - Connection state management                              │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                  Repository Layer                           │
├─────────────────────────────────────────────────────────────┤
│  ChatRepository                                             │
│  - REST API integration                                     │
│  - WebSocket message handling                               │
│  - Local database caching                                   │
│  - Offline mode support                                     │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
┌───────▼──────────┐   ┌─▼──────────┐  ┌─▼──────────────┐
│  REST API        │   │ WebSocket  │  │ Local Database │
│  (Retrofit)      │   │ Manager    │  │ (Room)         │
├──────────────────┤   ├────────────┤  ├────────────────┤
│ • Chats          │   │ • Connect  │  │ • Chats        │
│ • Messages       │   │ • Send MSG │  │ • Messages     │
│ • Users          │   │ • Typing   │  │ • Search       │
│ • Status         │   │ • Status   │  │ • Offline View │
└──────────────────┘   └────────────┘  └────────────────┘
```

---

## Data Flow for Message Sending

### Current (REST Only)
```
User Types & Sends
        │
        ▼
ViewModel Updates
        │
        ▼
Repository API Call
        │
        ▼
Server Response
        │
        ▼
UI Updates
```

### Future (REST + WebSocket)
```
User Types & Sends
        │
        ▼
ViewModel Updates
        │
    ┌───┴───┐
    │       │
    ▼       ▼
  REST    WebSocket
  API     Send
    │       │
    ▼       ▼
Local DB  Server
Cache     Updates
    │       │
    ├───────┤
    │       │
    ▼       ▼
Real-time Updates
    │
    ▼
UI Updates
```

---

## Message Lifecycle

```
SENDING STATE:
┌──────────┐
│ CREATING │  - Message created locally
└────┬─────┘
     │
┌────▼──────┐
│ SENDING   │  - REST API call initiated
└────┬──────┘
     │
┌────▼────────┐
│ SENT        │  - Server confirmed receipt
└────┬────────┘
     │
┌────▼──────────┐
│ DELIVERED     │  - Recipient got the message
└────┬──────────┘
     │
┌────▼───┐
│ READ    │  - Recipient read the message
└─────────┘
```

**WebSocket Events Needed:**
```
CLIENT EVENTS:
✓ message:send      - Send message via WebSocket
✓ typing:start      - User started typing
✓ typing:stop       - User stopped typing
✓ message:read      - Message read receipt
✓ status:online     - User online
✓ status:offline    - User offline
✓ presence:update   - Update presence

SERVER EVENTS:
✓ message:received  - New message incoming
✓ message:sent      - Message sent confirmation
✓ message:delivered - Message delivered
✓ message:read      - Message was read
✓ user:typing       - User is typing
✓ user:online       - User came online
✓ user:offline      - User went offline
✓ error             - Error occurred
```

---

## Component Interaction Diagram

```
┌──────────────────────────────────────────────────────────────┐
│                        CHATLIB LIBRARY                        │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    ChatLibConfig                         │ │
│  │  - API URLs                                              │ │
│  │  - WebSocket URLs                                        │ │
│  │  - Auth Token                                            │ │
│  │  - User ID                                               │ │
│  │  - Feature Flags                                         │ │
│  └──────────────────┬──────────────────────────────────────┘ │
│                     │                                         │
│  ┌──────────────────▼──────────────────────────────────────┐ │
│  │                ChatLibInitializer                        │ │
│  │  - Setup DI                                              │ │
│  │  - Init Database                                         │ │
│  │  - Connect WebSocket                                     │ │
│  │  - Restore Offline Messages                              │ │
│  └──────────────────┬──────────────────────────────────────┘ │
│                     │                                         │
│  ┌──────────────────▼──────────────────────────────────────┐ │
│  │            ChatViewModel (MVVM)                          │ │
│  ├─────────────────────────────────────────────────────────┤ │
│  │ StateFlows:                                              │ │
│  │  - messages: List<Message>                               │ │
│  │  - chats: List<Chat>                                     │ │
│  │  - isTyping: Boolean                                     │ │
│  │  - connectionState: ConnectionState                      │ │
│  │  - unreadCount: Int                                      │ │
│  │  - error: String?                                        │ │
│  │                                                           │ │
│  │ Functions:                                               │ │
│  │  - sendMessage(content: String)                          │ │
│  │  - loadMessages(chatId: String)                          │ │
│  │  - sendTyping(isTyping: Boolean)                         │ │
│  │  - markAsRead(messageId: String)                         │ │
│  │  - createChat(recipients: List<String>)                  │ │
│  └──────────────────┬──────────────────────────────────────┘ │
│                     │                                         │
│  ┌──────────────────▼──────────────────────────────────────┐ │
│  │            ChatRepository                                │ │
│  ├─────────────────────────────────────────────────────────┤ │
│  │ Handles:                                                 │ │
│  │  - REST API calls (Retrofit)                             │ │
│  │  - WebSocket events (WebSocketManager)                   │ │
│  │  - Database operations (Room DAO)                        │ │
│  │  - Conflict resolution                                   │ │
│  │  - Offline message queuing                               │ │
│  └──────────────────┬──────────────────────────────────────┘ │
│                     │                                         │
│        ┌────────────┼────────────┐                            │
│        │            │            │                            │
│  ┌─────▼──────┐ ┌──▼───────┐ ┌─▼──────────┐               │
│  │  REST API  │ │ WebSocket │ │  Database  │               │
│  │ (Retrofit) │ │ (Manager) │ │   (Room)   │               │
│  └────────────┘ └───────────┘ └────────────┘               │
│                                                                │
└──────────────────────────────────────────────────────────────┘
         │                    │                      │
         ▼                    ▼                      ▼
    ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐
    │  HTTP REST  │  │   WebSocket  │  │  SQLite Database │
    │   Backend   │  │    Backend   │  │  (Local Device)  │
    └─────────────┘  └──────────────┘  └──────────────────┘
```

---

## WebSocket Connection Lifecycle

```
START
  │
  ▼
┌─────────────────────────────┐
│   Initialize WebSocket      │
│   - Create OkHttpClient     │
│   - Set up listeners        │
│   - Load config             │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│   Connect to Server         │
│   - Build WebSocket request │
│   - Include auth token      │
│   - Set URL from config     │
└──────────┬──────────────────┘
           │
           ▼
    ┌──────────────┐
    │ Connection   │
    │ Successful?  │
    └────┬─────┬──┘
         │YES  │NO
         │     │
    ┌────▼┐ ┌─▼──────────────┐
    │ ✓   │ │ Retry Logic    │
    │CONN │ │ with Backoff   │
    │ECTED│ │ (Exp: 1,2,4,8) │
    └──┬──┘ └─┬──────────────┘
       │      │
       │  ┌───┘
       │  │  Max retries
       │  │  exceeded?
       │  │
       │  ▼
       │ ┌──────────────────┐
       │ │ Set State:       │
       │ │ CONNECTION_ERROR │
       │ └──────────────────┘
       │
       ▼
┌──────────────────────────────┐
│ CONNECTED STATE              │
│ Listen for:                  │
│ - Server Messages            │
│ - Connection Errors          │
│ - Graceful Close             │
└──────────┬───────────────────┘
           │
    ┌──────┴──────┬──────────┐
    │             │          │
    ▼             ▼          ▼
┌────────┐  ┌──────────┐  ┌──────────┐
│Message │  │   Error  │  │  Close   │
│Received│  │ Received │  │ Received │
└────┬───┘  └────┬─────┘  └────┬─────┘
     │           │             │
     ▼           ▼             ▼
  Process   Handle Error   Cleanup
   Event    Reconnect?     Resources
     │           │             │
     └───────────┼─────────────┘
              (loop)
```

---

## Offline Mode Flow

```
User sends message
        │
        ▼
Is Network Available?
    │         │
   YES       NO
    │         │
    ▼         ▼
Send via   Queue Message
WebSocket  in Database
    │         │
    │         ├─ Set status: PENDING
    │         │
    │         └─ Show to user as "pending"
    │
    ▼
Message Delivered?
    │         │
   YES       NO
    │         │
    ▼         ▼
Update DB  Retry with
Status     Backoff
    │
    ▼
Show as "sent"

Later... Network comes back online
        │
        ▼
┌─────────────────────┐
│ Reconnect Handler   │
└────────┬────────────┘
         │
         ▼
┌──────────────────────────────┐
│ Check for Pending Messages   │
│ in Database                  │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────────────────┐
│ Send Each Pending Message    │
│ Maintain order (by timestamp)│
└────────┬─────────────────────┘
         │
         ▼
All Sent? Mark all as sent
```

---

## Database Schema (Room)

```
ChatEntity
├── id: String (PK)
├── name: String
├── description: String?
├── avatar: String?
├── type: String (ONE_ON_ONE, GROUP, CHANNEL)
├── lastMessage: String?
├── lastMessageTime: Long?
├── unreadCount: Int
├── isActive: Boolean
└── syncedAt: Long

MessageEntity
├── id: String (PK)
├── chatId: String (FK -> ChatEntity)
├── senderId: String
├── senderName: String
├── senderAvatar: String?
├── text: String
├── type: String (TEXT, IMAGE, VIDEO, AUDIO, FILE)
├── timestamp: Long
├── isRead: Boolean
├── readAt: Long?
├── status: String (SENDING, SENT, DELIVERED, READ, FAILED)
├── isLocal: Boolean
├── retryCount: Int
└── syncedAt: Long

ChatMemberEntity
├── id: String (PK)
├── chatId: String (FK -> ChatEntity)
├── memberId: String
├── name: String
├── avatar: String?
├── role: String
└── joinedAt: Long
```

---

## Error Handling Strategy

```
┌──────────────────────────────────────┐
│         Error Occurrence             │
└────────────┬─────────────────────────┘
             │
             ▼
      ┌─────────────────┐
      │ Error Type?     │
      └─┬─┬─┬───┬────┬──┘
        │ │ │   │    │
    ┌───┘ │ │   │    └──── UNKNOWN
    │   ┌─┘ │   └──── RATE_LIMIT
    │   │ ┌─┘
    │   │ └──── NETWORK
    │   └────── SERVER
    │
    ▼ NETWORK_ERROR
┌────────────────────┐
│ Queue Message &    │
│ Auto-Retry Later   │
└────────────────────┘

    ▼ SERVER_ERROR
┌────────────────────┐
│ Show Error Message │
│ Offer Manual Retry │
└────────────────────┘

    ▼ RATE_LIMIT
┌────────────────────┐
│ Exponential Backoff│
│ User Notification  │
└────────────────────┘

    ▼ AUTHENTICATION
┌────────────────────┐
│ Refresh Token or   │
│ Re-authenticate    │
└────────────────────┘

    ▼ UNKNOWN
┌────────────────────┐
│ Log Error          │
│ Show Generic Msg   │
└────────────────────┘
```

---

## Testing Strategy

```
Unit Tests
├── ChatRepository Tests
│   ├── API call handling
│   ├── Error handling
│   └── Database operations
├── WebSocketManager Tests
│   ├── Connection lifecycle
│   ├── Message parsing
│   └── Reconnection logic
└── ChatViewModel Tests
    ├── State updates
    ├── Message sending
    └── Event handling

Integration Tests
├── REST + Database
├── WebSocket + Repository
└── E2E scenarios

UI Tests (Compose)
├── ChatScreen rendering
├── Message display
└── Input handling
```

---

## Deployment & Publishing

```
1. VERSION MANAGEMENT
   - Semantic Versioning (1.0.0)
   - Update CHANGELOG.md
   - Tag releases in Git

2. MAVEN CENTRAL
   - Setup signing keys
   - Configure build.gradle
   - Deploy via Sonatype

3. JITPACK (Alternative)
   - Automatic CI/CD integration
   - No complex setup needed
   - Good for private repos

4. GITHUB PACKAGES
   - For org-specific repos
   - Requires authentication

5. DOCUMENTATION SITE
   - Host on GitHub Pages
   - Auto-generate from KDoc
   - Sample apps & examples
```

---

## Performance Metrics to Track

```
Network
├── Message send latency
├── WebSocket connection time
└── API response times

Database
├── Query performance
├── Storage usage
└── Sync conflicts

UI
├── Recomposition frequency
├── Memory usage
├── CPU usage

Reliability
├── Connection uptime
├── Message delivery rate
└── Error rates
```

---

## Security Checklist

```
Authentication
☐ JWT token validation
☐ Token refresh mechanism
☐ Session management
☐ Logout handling

Data Protection
☐ Encrypt tokens at rest
☐ Use HTTPS/WSS only
☐ Message encryption (optional E2E)
☐ Input validation

Access Control
☐ User permission checks
☐ Chat access validation
☐ Message ownership verification
☐ Rate limiting

Compliance
☐ Data retention policies
☐ GDPR compliance
☐ Audit logging
☐ Privacy policies
```

