package com.devsneha.chatlib.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Chat(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("type")
    val type: ChatType = ChatType.ONE_ON_ONE,
    @SerializedName("members")
    val members: List<ChatMember> = emptyList(),
    @SerializedName("lastMessage")
    val lastMessage: String? = null,
    @SerializedName("lastMessageTime")
    val lastMessageTime: Long? = null,
    @SerializedName("unreadCount")
    val unreadCount: Int = 0,
    @SerializedName("createdAt")
    val createdAt: Long,
    @SerializedName("updatedAt")
    val updatedAt: Long? = null,
    @SerializedName("isActive")
    val isActive: Boolean = true
) : Serializable

data class ChatMember(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("role")
    val role: String = "member", // "admin", "member"
    @SerializedName("joinedAt")
    val joinedAt: Long
) : Serializable

enum class ChatType(val value: String) {
    ONE_ON_ONE("one_on_one"),
    GROUP("group"),
    CHANNEL("channel")
}
