package com.devsneha.chatlib.model

import com.devsneha.chatlib.ui.status.MessageStatus
import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class ChatMessageType {
    TEXT, IMAGE, VIDEO, AUDIO, FILE
}

data class ChatSender(
    val id: String,
    val name: String,
    val avatarUrl: String? = null
)

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val content: String,
    val type: ChatMessageType = ChatMessageType.TEXT,
    val timestamp: Long,
    val isMine: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val replyTo: String? = null // ID of message this is replying to
)

// API Message Model
data class Message(
    @SerializedName("id")
    val id: String,
    @SerializedName("chatId")
    val chatId: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("senderName")
    val senderName: String,
    @SerializedName("senderAvatar")
    val senderAvatar: String? = null,
    @SerializedName("text")
    val text: String,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("isRead")
    val isRead: Boolean = false,
    @SerializedName("attachments")
    val attachments: List<Attachment>? = null
) : Serializable {
    companion object {
        fun createLocalMessage(
            chatId: String,
            senderId: String,
            senderName: String,
            text: String
        ): Message {
            return Message(
                id = System.currentTimeMillis().toString(),
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                text = text,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        }
    }
}

data class Attachment(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String, // "image", "video", "document"
    @SerializedName("url")
    val url: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("size")
    val size: Long? = null
) : Serializable
