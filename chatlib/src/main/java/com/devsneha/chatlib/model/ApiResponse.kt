package com.devsneha.chatlib.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("error")
    val error: String? = null
)

data class PaginatedResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<T> = emptyList(),
    @SerializedName("pagination")
    val pagination: PaginationInfo? = null
)

data class PaginationInfo(
    @SerializedName("page")
    val page: Int,
    @SerializedName("pageSize")
    val pageSize: Int,
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("totalPages")
    val totalPages: Int
)

// Request models
data class SendMessageRequest(
    @SerializedName("chatId")
    val chatId: String,
    @SerializedName("senderId")
    val senderId: String,
    @SerializedName("senderName")
    val senderName: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("attachments")
    val attachments: List<Attachment>? = null
)

data class CreateChatRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("type")
    val type: String, // "one_on_one", "group", "channel"
    @SerializedName("memberIds")
    val memberIds: List<String>,
    @SerializedName("avatar")
    val avatar: String? = null
)

data class MarkAsReadRequest(
    @SerializedName("messageIds")
    val messageIds: List<String>
)
