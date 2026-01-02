# Backend Requirements for ChatLib

## Quick Reference for Backend Team

This document outlines all the APIs, WebSocket events, and data models that the backend needs to provide for ChatLib to function as a complete chat library.

---

## Table of Contents

1. [Authentication](#authentication)
2. [REST API Endpoints](#rest-api-endpoints)
3. [WebSocket Protocol](#websocket-protocol)
4. [Data Models](#data-models)
5. [Error Responses](#error-responses)
6. [Rate Limiting](#rate-limiting)
7. [Performance Requirements](#performance-requirements)

---

## Authentication

### JWT Token Flow

```
1. User Login → Backend returns JWT token + Refresh token
2. Store JWT securely on device
3. Add JWT to Authorization header for all requests
4. On token expiry → Use refresh token to get new JWT
5. If refresh fails → Redirect to login
```

### Required Headers

```
Authorization: Bearer {jwt_token}
Content-Type: application/json
X-Device-ID: {unique_device_id} (optional, for tracking)
X-App-Version: {app_version} (optional, for versioning)
```

### Token Refresh Endpoint

```
POST /api/v1/auth/refresh-token
Content-Type: application/json

Request:
{
  "refreshToken": "refresh_token_here"
}

Response (200):
{
  "success": true,
  "data": {
    "accessToken": "new_jwt_token",
    "refreshToken": "new_refresh_token",
    "expiresIn": 3600
  }
}

Response (401):
{
  "success": false,
  "error": "INVALID_REFRESH_TOKEN",
  "message": "Refresh token is invalid or expired"
}
```

---

## REST API Endpoints

### Chat Operations

#### 1. Get User's Chats (with Pagination)

```
GET /api/v1/chats?page=1&pageSize=20&search=john
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": [
    {
      "id": "chat_123",
      "name": "John Doe",
      "description": "Personal DM",
      "avatar": "https://...image.jpg",
      "type": "ONE_ON_ONE",
      "members": [
        {
          "id": "user_1",
          "name": "You",
          "avatar": "https://...",
          "role": "member",
          "joinedAt": 1609459200000
        },
        {
          "id": "user_2",
          "name": "John Doe",
          "avatar": "https://...",
          "role": "member",
          "joinedAt": 1609459200000
        }
      ],
      "lastMessage": "See you tomorrow!",
      "lastMessageTime": 1609545600000,
      "unreadCount": 2,
      "createdAt": 1609459200000,
      "updatedAt": 1609545600000,
      "isActive": true
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 50,
    "totalPages": 3
  }
}

Response (401): Unauthorized
Response (500): Server Error
```

#### 2. Get Chat Details

```
GET /api/v1/chats/{chatId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": {
    "id": "chat_123",
    "name": "Project Team",
    "description": "Discussing project deadlines",
    "avatar": "https://...group_image.jpg",
    "type": "GROUP",
    "members": [
      {
        "id": "user_1",
        "name": "John",
        "avatar": "https://...",
        "role": "admin",
        "joinedAt": 1609459200000
      },
      {
        "id": "user_2",
        "name": "Jane",
        "avatar": "https://...",
        "role": "member",
        "joinedAt": 1609470000000
      }
    ],
    "lastMessage": "Meeting at 3 PM",
    "lastMessageTime": 1609545600000,
    "unreadCount": 0,
    "createdAt": 1609459200000,
    "updatedAt": 1609545600000,
    "isActive": true
  }
}

Response (404): Chat not found
```

#### 3. Create New Chat (1-on-1 or Group)

```
POST /api/v1/chats
Authorization: Bearer {token}
Content-Type: application/json

Request for 1-on-1:
{
  "type": "ONE_ON_ONE",
  "recipientId": "user_456"
}

Request for Group:
{
  "type": "GROUP",
  "name": "Project Team",
  "description": "Team discussion",
  "avatar": "https://...image.jpg",
  "memberIds": ["user_2", "user_3", "user_4"]
}

Response (201):
{
  "success": true,
  "data": {
    "id": "chat_new_123",
    "name": "John Doe",
    "type": "ONE_ON_ONE",
    "members": [...],
    "createdAt": 1609632000000,
    "isActive": true
  }
}

Response (400): Invalid request
Response (409): Chat already exists
```

#### 4. Update Chat (Group only)

```
PATCH /api/v1/chats/{chatId}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "name": "New Group Name",
  "description": "Updated description",
  "avatar": "https://...new_image.jpg"
}

Response (200):
{
  "success": true,
  "data": { ...updated chat object... }
}

Response (403): Not a group or no permission
Response (404): Chat not found
```

#### 5. Delete Chat

```
DELETE /api/v1/chats/{chatId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Chat deleted successfully"
}

Response (403): No permission to delete
Response (404): Chat not found
```

### Message Operations

#### 1. Get Messages (with Pagination & Backward Pagination)

```
GET /api/v1/chats/{chatId}/messages?page=1&pageSize=50&before={timestamp}
Authorization: Bearer {token}

Query Parameters:
- page: Page number (default 1)
- pageSize: Messages per page (default 50, max 100)
- before: Get messages before this timestamp (for backward loading)
- after: Get messages after this timestamp (for forward loading)

Response (200):
{
  "success": true,
  "data": [
    {
      "id": "msg_123",
      "chatId": "chat_123",
      "senderId": "user_1",
      "senderName": "John",
      "senderAvatar": "https://...",
      "text": "Hey, how are you?",
      "type": "TEXT",
      "timestamp": 1609545600000,
      "isRead": true,
      "readAt": 1609546000000,
      "status": "READ",
      "attachments": null,
      "replyTo": null
    },
    {
      "id": "msg_124",
      "chatId": "chat_123",
      "senderId": "user_2",
      "senderName": "Jane",
      "senderAvatar": "https://...",
      "text": "Good! How about you?",
      "type": "TEXT",
      "timestamp": 1609546000000,
      "isRead": false,
      "readAt": null,
      "status": "SENT",
      "attachments": null,
      "replyTo": "msg_123"
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 50,
    "totalItems": 200,
    "hasMore": true
  }
}

Response (404): Chat not found
```

#### 2. Send Message

```
POST /api/v1/chats/{chatId}/messages
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "text": "Hello everyone!",
  "type": "TEXT",
  "attachments": [
    {
      "url": "https://...image.jpg",
      "type": "image",
      "mimeType": "image/jpeg"
    }
  ],
  "replyTo": "msg_120" (optional)
}

Response (201):
{
  "success": true,
  "data": {
    "id": "msg_125",
    "chatId": "chat_123",
    "senderId": "user_1",
    "senderName": "John",
    "senderAvatar": "https://...",
    "text": "Hello everyone!",
    "type": "TEXT",
    "timestamp": 1609546800000,
    "isRead": false,
    "status": "SENT",
    "attachments": [
      {
        "id": "att_1",
        "url": "https://...image.jpg",
        "type": "image",
        "mimeType": "image/jpeg"
      }
    ]
  }
}

Response (400): Invalid message
Response (403): No permission to send in this chat
Response (404): Chat not found
Response (413): Message too large
```

#### 3. Mark Messages as Read

```
POST /api/v1/chats/{chatId}/read
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "messageIds": ["msg_123", "msg_124", "msg_125"]
}

Response (200):
{
  "success": true,
  "message": "Messages marked as read"
}

Also send via WebSocket:
{
  "action": "MESSAGE_READ",
  "chatId": "chat_123",
  "messageIds": ["msg_123", "msg_124"],
  "readBy": "user_1",
  "timestamp": 1609546800000
}
```

#### 4. Get Unread Count

```
GET /api/v1/chats/{chatId}/unread
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": {
    "unreadCount": 5,
    "lastReadMessageId": "msg_120"
  }
}
```

#### 5. Update/Edit Message

```
PATCH /api/v1/messages/{messageId}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "text": "Updated message text"
}

Response (200):
{
  "success": true,
  "data": {
    ...updated message with "edited" flag...
    "isEdited": true,
    "editedAt": 1609546900000
  }
}

Also broadcast via WebSocket
```

#### 6. Delete Message

```
DELETE /api/v1/messages/{messageId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Message deleted"
}

Also broadcast via WebSocket
```

### User Operations

#### 1. Get User Profile

```
GET /api/v1/users/{userId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": {
    "id": "user_123",
    "name": "John Doe",
    "phone": "+1234567890",
    "avatar": "https://...avatar.jpg",
    "bio": "Software Engineer",
    "status": "ONLINE",
    "lastSeen": 1609546800000,
    "createdAt": 1609459200000,
    "isBlocked": false
  }
}

Response (404): User not found
```

#### 2. Search Users

```
GET /api/v1/users/search?q=john&limit=10
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": [
    {
      "id": "user_1",
      "name": "John Doe",
      "avatar": "https://...",
      "status": "ONLINE",
      "isContact": true
    },
    {
      "id": "user_2",
      "name": "John Smith",
      "avatar": "https://...",
      "status": "AWAY",
      "isContact": false
    }
  ]
}
```

#### 3. Update User Profile

```
PUT /api/v1/users/{userId}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "name": "John Updated",
  "bio": "Senior Engineer",
  "avatar": "https://...new_avatar.jpg"
}

Response (200):
{
  "success": true,
  "data": { ...updated user... }
}
```

#### 4. Get User Status

```
GET /api/v1/users/{userId}/status
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "data": {
    "userId": "user_123",
    "status": "ONLINE",
    "lastSeen": 1609546800000
  }
}
```

### Group Management

#### 1. Add Member to Group

```
POST /api/v1/chats/{chatId}/members
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "userId": "user_new_456"
}

Response (201):
{
  "success": true,
  "data": {
    "id": "user_new_456",
    "name": "New Member",
    "avatar": "https://...",
    "role": "member",
    "joinedAt": 1609546800000
  }
}
```

#### 2. Remove Member from Group

```
DELETE /api/v1/chats/{chatId}/members/{userId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Member removed"
}
```

#### 3. Update Member Role

```
PUT /api/v1/chats/{chatId}/members/{userId}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "role": "admin" or "member"
}

Response (200):
{
  "success": true,
  "data": { ...updated member... }
}
```

---

## WebSocket Protocol

### Connection

```
WebSocket URL: wss://your-api.com/api/v1/ws
Query Parameters:
  ?token={jwt_token}&userId={userId}

Headers:
  Authorization: Bearer {jwt_token}
  User-Agent: {app_identifier}
```

### Message Format

All WebSocket messages follow this structure:

```json
{
  "id": "unique_message_id",
  "action": "ACTION_NAME",
  "chatId": "chat_id",
  "userId": "user_id",
  "timestamp": 1609546800000,
  "data": { /* action-specific data */ }
}
```

### Client-to-Server Actions

#### 1. SEND_MESSAGE

```json
{
  "action": "SEND_MESSAGE",
  "chatId": "chat_123",
  "text": "Hello!",
  "type": "TEXT",
  "clientMessageId": "temp_msg_123",
  "replyTo": "msg_120",
  "timestamp": 1609546800000
}

Response from Server:
{
  "action": "MESSAGE_SENT",
  "clientMessageId": "temp_msg_123",
  "messageId": "msg_125",
  "status": "SENT",
  "timestamp": 1609546800000
}
```

#### 2. TYPING

```json
{
  "action": "TYPING",
  "chatId": "chat_123",
  "isTyping": true,
  "timestamp": 1609546800000
}
```

#### 3. READ_RECEIPT

```json
{
  "action": "READ_RECEIPT",
  "chatId": "chat_123",
  "messageIds": ["msg_123", "msg_124"],
  "timestamp": 1609546800000
}
```

#### 4. USER_STATUS

```json
{
  "action": "USER_STATUS",
  "status": "ONLINE|AWAY|OFFLINE",
  "timestamp": 1609546800000
}
```

#### 5. PING (Keep-Alive)

```json
{
  "action": "PING"
}

Server responds with:
{
  "action": "PONG"
}
```

### Server-to-Client Actions

#### 1. MESSAGE_RECEIVED

```json
{
  "action": "MESSAGE_RECEIVED",
  "chatId": "chat_123",
  "message": {
    "id": "msg_125",
    "chatId": "chat_123",
    "senderId": "user_1",
    "senderName": "John",
    "senderAvatar": "https://...",
    "text": "Hello!",
    "type": "TEXT",
    "timestamp": 1609546800000,
    "status": "SENT"
  }
}
```

#### 2. USER_TYPING

```json
{
  "action": "USER_TYPING",
  "chatId": "chat_123",
  "userId": "user_2",
  "userName": "Jane",
  "isTyping": true,
  "timestamp": 1609546800000
}
```

#### 3. MESSAGE_READ

```json
{
  "action": "MESSAGE_READ",
  "chatId": "chat_123",
  "messageId": "msg_125",
  "readBy": "user_2",
  "readAt": 1609546900000,
  "timestamp": 1609546900000
}
```

#### 4. USER_STATUS_CHANGED

```json
{
  "action": "USER_STATUS_CHANGED",
  "userId": "user_2",
  "status": "ONLINE|AWAY|OFFLINE",
  "lastSeen": 1609546900000,
  "timestamp": 1609546900000
}
```

#### 5. MESSAGE_DELIVERED

```json
{
  "action": "MESSAGE_DELIVERED",
  "messageId": "msg_125",
  "deliveredAt": 1609546850000,
  "timestamp": 1609546850000
}
```

#### 6. ERROR

```json
{
  "action": "ERROR",
  "code": "INVALID_MESSAGE|UNAUTHORIZED|RATE_LIMITED",
  "message": "Detailed error message",
  "timestamp": 1609546800000
}
```

#### 7. CONNECTION_ESTABLISHED

```json
{
  "action": "CONNECTION_ESTABLISHED",
  "userId": "user_1",
  "timestamp": 1609546800000,
  "serverTime": 1609546800000
}
```

---

## Data Models

### Chat Model

```json
{
  "id": "string (UUID)",
  "name": "string",
  "description": "string | null",
  "avatar": "string (URL) | null",
  "type": "ONE_ON_ONE | GROUP | CHANNEL",
  "members": [
    {
      "id": "string",
      "name": "string",
      "avatar": "string (URL) | null",
      "role": "admin | moderator | member",
      "joinedAt": "number (timestamp)"
    }
  ],
  "lastMessage": "string | null",
  "lastMessageTime": "number (timestamp) | null",
  "unreadCount": "number",
  "createdAt": "number (timestamp)",
  "updatedAt": "number (timestamp) | null",
  "isActive": "boolean"
}
```

### Message Model

```json
{
  "id": "string (UUID)",
  "chatId": "string",
  "senderId": "string",
  "senderName": "string",
  "senderAvatar": "string (URL) | null",
  "text": "string",
  "type": "TEXT | IMAGE | VIDEO | AUDIO | FILE",
  "timestamp": "number (milliseconds)",
  "isRead": "boolean",
  "readAt": "number (timestamp) | null",
  "status": "SENDING | SENT | DELIVERED | READ | FAILED",
  "attachments": [
    {
      "id": "string",
      "url": "string",
      "type": "image | video | audio | file",
      "mimeType": "string",
      "size": "number (bytes) | null",
      "name": "string | null"
    }
  ] | null,
  "replyTo": "string (messageId) | null",
  "isEdited": "boolean",
  "editedAt": "number (timestamp) | null"
}
```

### User Model

```json
{
  "id": "string (UUID)",
  "name": "string",
  "phone": "string",
  "avatar": "string (URL) | null",
  "bio": "string | null",
  "status": "ONLINE | AWAY | OFFLINE",
  "lastSeen": "number (timestamp)",
  "createdAt": "number (timestamp)",
  "isBlocked": "boolean",
  "blockedBy": ["string (userId)"]
}
```

---

## Error Responses

### Standard Error Response Format

```json
{
  "success": false,
  "error": "ERROR_CODE",
  "message": "Human readable error message",
  "statusCode": 400,
  "timestamp": 1609546800000,
  "path": "/api/v1/endpoint"
}
```

### Common HTTP Status Codes

| Status | Meaning | Example |
|--------|---------|---------|
| 200 | OK | Successful GET request |
| 201 | Created | Message sent successfully |
| 400 | Bad Request | Invalid JSON or parameters |
| 401 | Unauthorized | Missing or invalid JWT |
| 403 | Forbidden | No permission to access |
| 404 | Not Found | Chat or message not found |
| 409 | Conflict | Chat already exists |
| 413 | Payload Too Large | Message/file too large |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Server Error | Internal server error |

### Common Error Codes

```
UNAUTHORIZED
├─ INVALID_TOKEN
├─ EXPIRED_TOKEN
├─ MISSING_TOKEN
└─ INVALID_CREDENTIALS

FORBIDDEN
├─ NO_PERMISSION
├─ USER_BLOCKED
└─ CHAT_ARCHIVED

NOT_FOUND
├─ CHAT_NOT_FOUND
├─ MESSAGE_NOT_FOUND
├─ USER_NOT_FOUND
└─ MEMBER_NOT_FOUND

VALIDATION_ERROR
├─ INVALID_MESSAGE
├─ MESSAGE_TOO_LONG
├─ FILE_TOO_LARGE
└─ INVALID_REQUEST

RATE_LIMITED
├─ TOO_MANY_MESSAGES
├─ TOO_MANY_REQUESTS
└─ SLOW_DOWN

INTERNAL_ERROR
├─ DATABASE_ERROR
├─ SERVICE_UNAVAILABLE
└─ UNKNOWN_ERROR
```

---

## Rate Limiting

### Rate Limit Headers

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 50
X-RateLimit-Reset: 1609547400
```

### Recommended Limits

```
Messages Per Minute: 30
Chats Per Minute: 10
User Search Per Minute: 20
File Upload Per Hour: 50
WebSocket Messages Per Minute: 100
```

---

## Performance Requirements

### Response Time SLAs

| Endpoint | Target | Acceptable |
|----------|--------|-----------|
| GET /chats | < 200ms | < 500ms |
| GET /messages | < 300ms | < 800ms |
| POST /messages | < 500ms | < 1000ms |
| WebSocket latency | < 100ms | < 200ms |

### Concurrent Connection Targets

- Support 10,000+ concurrent WebSocket connections per server
- Support 100+ simultaneous message sends per user
- Database: 1000+ concurrent connections

### Storage Targets

- Message retention: Configurable (default 1 year)
- File storage: AWS S3 or similar CDN
- Database cleanup: Archival after 2 years

---

## Testing Data

### Test Users

```json
{
  "users": [
    {
      "id": "test_user_1",
      "phone": "+1111111111",
      "name": "Test User 1",
      "password": "test123456"
    },
    {
      "id": "test_user_2",
      "phone": "+2222222222",
      "name": "Test User 2",
      "password": "test123456"
    }
  ]
}
```

### Test Endpoints

```
Development: https://dev-api.example.com
Staging: https://staging-api.example.com
Production: https://api.example.com

WebSocket (Dev): wss://dev-api.example.com/ws
WebSocket (Staging): wss://staging-api.example.com/ws
WebSocket (Prod): wss://api.example.com/ws
```

---

## Implementation Checklist for Backend

- [ ] Authentication (JWT, Refresh tokens)
- [ ] Chat CRUD operations
- [ ] Message CRUD operations
- [ ] User profile operations
- [ ] WebSocket server setup
- [ ] All WebSocket actions implemented
- [ ] Error handling with standard format
- [ ] Rate limiting
- [ ] Database indexing for performance
- [ ] File upload/storage integration
- [ ] Message encryption (optional)
- [ ] Comprehensive logging
- [ ] API documentation (Swagger/OpenAPI)
- [ ] Unit tests (80%+ coverage)
- [ ] Integration tests
- [ ] Load testing
- [ ] Security review
- [ ] GDPR compliance

