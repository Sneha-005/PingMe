package com.devsneha.chatlib.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.chatlib.model.Chat
import com.devsneha.chatlib.model.CreateChatRequest
import com.devsneha.chatlib.model.Message
import com.devsneha.chatlib.model.PaginatedResponse
import com.devsneha.chatlib.model.SendMessageRequest
import com.devsneha.chatlib.repository.ChatRepository
import com.devsneha.chatlib.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatListUiState(
    val isLoading: Boolean = false,
    val chats: List<Chat> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

data class ChatDetailUiState(
    val isLoading: Boolean = false,
    val chat: Chat? = null,
    val error: String? = null
)

data class MessageListUiState(
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

data class SendMessageUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val sentMessage: Message? = null
)

data class TypingIndicatorState(
    val isTyping: Boolean = false,
    val typingUsers: Set<String> = emptySet()
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    
    private val _chatListState = MutableStateFlow(ChatListUiState())
    val chatListState: StateFlow<ChatListUiState> = _chatListState.asStateFlow()
    
    fun loadChats(userId: String, page: Int = 1) {
        viewModelScope.launch {
            repository.getChats(userId = userId, page = page).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _chatListState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        val paginatedResponse = result.data
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                chats = if (page == 1) paginatedResponse.data else it.chats + paginatedResponse.data,
                                currentPage = page,
                                hasMore = paginatedResponse.pagination?.page ?: 1 < paginatedResponse.pagination?.totalPages ?: 1
                            )
                        }
                    }
                    is Result.Error -> {
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun searchChats(query: String, userId: String) {
        viewModelScope.launch {
            repository.searchChats(query, userId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _chatListState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                chats = result.data.data
                            )
                        }
                    }
                    is Result.Error -> {
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun createChat(request: CreateChatRequest) {
        viewModelScope.launch {
            repository.createChat(request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _chatListState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                chats = listOf(result.data) + it.chats
                            )
                        }
                    }
                    is Result.Error -> {
                        _chatListState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            repository.deleteChat(chatId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _chatListState.update {
                            it.copy(
                                chats = it.chats.filter { chat -> chat.id != chatId }
                            )
                        }
                    }
                    is Result.Error -> {
                        _chatListState.update {
                            it.copy(error = result.message)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    
    private val _chatDetailState = MutableStateFlow(ChatDetailUiState())
    val chatDetailState: StateFlow<ChatDetailUiState> = _chatDetailState.asStateFlow()
    
    fun loadChatDetails(chatId: String) {
        viewModelScope.launch {
            repository.getChatDetails(chatId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _chatDetailState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _chatDetailState.update {
                            it.copy(
                                isLoading = false,
                                chat = result.data
                            )
                        }
                    }
                    is Result.Error -> {
                        _chatDetailState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class MessageListViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    
    private val _messageListState = MutableStateFlow(MessageListUiState())
    val messageListState: StateFlow<MessageListUiState> = _messageListState.asStateFlow()
    
    private val _typingState = MutableStateFlow(TypingIndicatorState())
    val typingState: StateFlow<TypingIndicatorState> = _typingState.asStateFlow()
    
    fun loadMessages(chatId: String, page: Int = 1, beforeTimestamp: Long? = null) {
        viewModelScope.launch {
            repository.getMessages(chatId = chatId, page = page, beforeTimestamp = beforeTimestamp)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _messageListState.update { it.copy(isLoading = true, error = null) }
                        }
                        is Result.Success -> {
                            val paginatedResponse = result.data
                            _messageListState.update {
                                it.copy(
                                    isLoading = false,
                                    messages = if (page == 1) paginatedResponse.data 
                                        else it.messages + paginatedResponse.data,
                                    currentPage = page,
                                    hasMore = paginatedResponse.pagination?.page ?: 1 < 
                                        paginatedResponse.pagination?.totalPages ?: 1
                                )
                            }
                        }
                        is Result.Error -> {
                            _messageListState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }
    
    fun searchMessages(chatId: String, query: String) {
        viewModelScope.launch {
            repository.searchMessages(chatId, query).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _messageListState.update { it.copy(isLoading = true, error = null) }
                    }
                    is Result.Success -> {
                        _messageListState.update {
                            it.copy(
                                isLoading = false,
                                messages = result.data.data
                            )
                        }
                    }
                    is Result.Error -> {
                        _messageListState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _messageListState.update {
                            it.copy(
                                messages = it.messages.filter { msg -> msg.id != messageId }
                            )
                        }
                    }
                    is Result.Error -> {
                        _messageListState.update {
                            it.copy(error = result.message)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    
    fun markAsRead(messageIds: List<String>) {
        viewModelScope.launch {
            repository.markMessagesAsRead(messageIds).collect { _ -> }
        }
    }
    
    fun setTypingIndicator(chatId: String, userId: String, isTyping: Boolean) {
        viewModelScope.launch {
            repository.sendTypingIndicator(chatId, userId, isTyping).collect { _ -> }
        }
    }
    
    fun addTypingUser(userId: String) {
        _typingState.update {
            it.copy(typingUsers = it.typingUsers + userId)
        }
    }
    
    fun removeTypingUser(userId: String) {
        _typingState.update {
            it.copy(typingUsers = it.typingUsers - userId)
        }
    }
}

@HiltViewModel
class SendMessageViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {
    
    private val _sendMessageState = MutableStateFlow(SendMessageUiState())
    val sendMessageState: StateFlow<SendMessageUiState> = _sendMessageState.asStateFlow()
    
    fun sendMessage(chatId: String, request: SendMessageRequest) {
        viewModelScope.launch {
            repository.sendMessage(chatId, request).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _sendMessageState.update { it.copy(isLoading = true, error = null, success = false) }
                    }
                    is Result.Success -> {
                        _sendMessageState.update {
                            it.copy(
                                isLoading = false,
                                success = true,
                                sentMessage = result.data,
                                error = null
                            )
                        }
                        // Reset after a delay
                        kotlinx.coroutines.delay(500)
                        resetState()
                    }
                    is Result.Error -> {
                        _sendMessageState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                                success = false
                            )
                        }
                    }
                }
            }
        }
    }
    
    fun resetState() {
        _sendMessageState.update { SendMessageUiState() }
    }
}
