package com.devsneha.pingme.model

import com.google.gson.annotations.SerializedName
import java.util.Date

// User model
data class User(
    val id: Long,
    val username: String,
    val phoneNumber: String? = null,
    val profilePicture: String? = null,
    val status: String? = null,
    val isOnline: Boolean = false,
    val lastSeen: Date? = null
)

// Message model for individual and group messages
data class Message(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long? = null,
    val receiverName: String? = null,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val timestamp: Date,
    val isRead: Boolean = false,
    val isDelivered: Boolean = false,
    val conversationId: Long? = null,
    val conversationName: String? = null,
    val action: String? = null
)

// Conversation model
data class Conversation(
    val id: Long,
    val name: String,
    val type: ConversationType,
    val description: String? = null,
    val createdBy: User,
    val createdAt: Date,
    val lastMessageAt: Date,
    val participants: List<User>
)

// Message types
enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    LOCATION
}

// Conversation types
enum class ConversationType {
    INDIVIDUAL,
    GROUP
}

// API Request/Response models

// Send individual message request
data class SendMessageRequest(
    val senderId: Long,
    val receiverId: Long,
    val content: String,
    val messageType: String = "TEXT"
)

// Send group message request
data class SendGroupMessageRequest(
    val senderId: Long,
    val conversationId: Long,
    val content: String,
    val messageType: String = "TEXT"
)

// Create group conversation request
data class CreateGroupRequest(
    val name: String,
    val description: String? = null,
    val creatorId: Long,
    val participantIds: List<Long>
)

// Paginated response wrapper
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)

// Typing indicator request
data class TypingRequest(
    val senderId: Long,
    val receiverId: Long,
    val isTyping: Boolean
)

// Online status request
data class StatusRequest(
    val userId: Long,
    val isOnline: Boolean
)

// WebSocket models
data class WebSocketMessage(
    val senderId: Long,
    val senderName: String? = null,
    val receiverId: Long? = null,
    val content: String? = null,
    val messageType: String? = null,
    val action: String,
    val conversationId: Long? = null
)

// WebSocket event types
enum class WebSocketEventType {
    SEND_MESSAGE,
    READ_MESSAGE,
    TYPING,
    STOP_TYPING,
    ONLINE,
    OFFLINE,
    ADD_USER,
    LEAVE_CHAT
}

// WebSocket destinations
object WebSocketDestinations {
    const val SEND_MESSAGE = "/app/chat.sendMessage"
    const val ADD_USER = "/app/chat.addUser"
    const val TYPING = "/app/chat.typing"
    const val READ = "/app/chat.read"
    const val LEAVE = "/app/chat.leave"
    
    // Subscriptions
    fun userMessages(userId: Long) = "/user/$userId/queue/messages"
    fun userTyping(userId: Long) = "/user/$userId/queue/typing"
    const val STATUS_TOPIC = "/topic/status"
    const val PUBLIC_TOPIC = "/topic/public"
}

// Error response model
data class ErrorResponse(
    val error: String,
    val message: String
) 