package com.devsneha.pingme.presentation.chat

import com.devsneha.pingme.model.Conversation

data class ChatListState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val currentPage: Int = 0,
    val hasMoreConversations: Boolean = true
) 