package com.devsneha.pingme.presentation.chat

import com.devsneha.pingme.model.Conversation
import com.devsneha.pingme.model.Message

data class ChatScreenState(
    val conversation: Conversation? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val messageText: String = "",
    val isSending: Boolean = false,
    val isTyping: Boolean = false,
    val otherUserTyping: Boolean = false,
    val hasMoreMessages: Boolean = true,
    val currentPage: Int = 0,
    val isIndividualChat: Boolean = true,
    val otherUserId: Long? = null
) 