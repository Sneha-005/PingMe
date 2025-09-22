package com.devsneha.pingme.presentation.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ChatRepository
import com.devsneha.pingme.data.repository.UserRepository
import com.devsneha.pingme.data.remote.WebSocketService
import com.devsneha.pingme.model.Conversation
import com.devsneha.pingme.model.Message
import com.devsneha.pingme.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ChatScreenViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val webSocketService: WebSocketService,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val conversationId: String? = savedStateHandle["conversationId"]
    private val otherUserId: String? = savedStateHandle["otherUserId"]
    
    private val _state = MutableStateFlow(ChatScreenState())
    val state: StateFlow<ChatScreenState> = _state.asStateFlow()
    
    private var currentUserId: Long = 1L // Default fallback
    private var currentUsername: String = ""

    init {
        setCurrentUser()
        setupChat()
        observeWebSocketEvents()
    }

    private fun setCurrentUser() {
        val user = userRepository.getUser()
        if (user != null) {
            // Try to get userId from AuthUser or fallback to username hash
            currentUsername = user.username
            currentUserId = user.username.hashCode().toLong().absoluteValue // Fallback if no id
        }
    }
    
    private fun setupChat() {
        if (conversationId != null) {
            // Group chat
            _state.update { it.copy(isIndividualChat = false) }
            loadConversationMessages(conversationId.toLong())
        } else if (otherUserId != null) {
            // Individual chat
            _state.update { 
                it.copy(
                    isIndividualChat = true,
                    otherUserId = otherUserId.toLong()
                )
            }
            loadIndividualMessages(currentUserId, otherUserId.toLong())
        }
    }
    
    private fun loadConversationMessages(conversationId: Long, page: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            chatRepository.getConversationMessages(conversationId, page)
                .onSuccess { paginatedResponse ->
                    _state.update { currentState ->
                        currentState.copy(
                            messages = if (page == 0) {
                                paginatedResponse.content
                            } else {
                                currentState.messages + paginatedResponse.content
                            },
                            isLoading = false,
                            currentPage = page,
                            hasMoreMessages = paginatedResponse.number < paginatedResponse.totalPages - 1
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            error = exception.message ?: "Failed to load messages",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    private fun loadIndividualMessages(user1Id: Long, user2Id: Long, page: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            chatRepository.getMessagesBetweenUsers(user1Id, user2Id, page)
                .onSuccess { messages ->
                    _state.update { currentState ->
                        currentState.copy(
                            messages = if (page == 0) {
                                messages
                            } else {
                                currentState.messages + messages
                            },
                            isLoading = false,
                            currentPage = page,
                            hasMoreMessages = messages.size >= 50 // Assuming 50 is the page size
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            error = exception.message ?: "Failed to load messages",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun onMessageTextChange(text: String) {
        _state.update { it.copy(messageText = text) }
    }
    
    fun sendMessage() {
        val messageText = _state.value.messageText.trim()
        if (messageText.isEmpty()) return
        
        viewModelScope.launch {
            _state.update { it.copy(isSending = true, messageText = "") }
            
            val result = if (_state.value.isIndividualChat) {
                val otherUserId = _state.value.otherUserId
                if (otherUserId != null) {
                    chatRepository.sendMessage(currentUserId, otherUserId, messageText)
                } else {
                    Result.failure(Exception("Other user ID not found"))
                }
            } else {
                val conversationId = conversationId?.toLong()
                if (conversationId != null) {
                    chatRepository.sendGroupMessage(currentUserId, conversationId, messageText)
                } else {
                    Result.failure(Exception("Conversation ID not found"))
                }
            }
            
            result.onSuccess { message ->
                _state.update { currentState ->
                    currentState.copy(
                        messages = currentState.messages + message,
                        isSending = false
                    )
                }
            }.onFailure { exception ->
                _state.update { 
                    it.copy(
                        error = exception.message ?: "Failed to send message",
                        isSending = false,
                        messageText = messageText // Restore the message text
                    )
                }
            }
        }
    }
    
    fun markMessagesAsRead() {
        if (_state.value.isIndividualChat) {
            val otherUserId = _state.value.otherUserId
            if (otherUserId != null) {
                viewModelScope.launch {
                    chatRepository.markMessagesAsRead(currentUserId, otherUserId)
                }
            }
        }
    }
    
    fun loadMoreMessages() {
        if (!_state.value.hasMoreMessages || _state.value.isLoading) return
        
        val nextPage = _state.value.currentPage + 1
        
        if (_state.value.isIndividualChat) {
            val otherUserId = _state.value.otherUserId
            if (otherUserId != null) {
                loadIndividualMessages(currentUserId, otherUserId, nextPage)
            }
        } else {
            val conversationId = conversationId?.toLong()
            if (conversationId != null) {
                loadConversationMessages(conversationId, nextPage)
            }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun setTypingStatus(isTyping: Boolean) {
        _state.update { it.copy(isTyping = isTyping) }
        
        if (_state.value.isIndividualChat) {
            val otherUserId = _state.value.otherUserId
            if (otherUserId != null) {
                viewModelScope.launch {
                    chatRepository.sendTypingIndicator(currentUserId, otherUserId, isTyping)
                }
            }
        }
    }
    
    fun addNewMessage(message: Message) {
        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + message
            )
        }
    }
    
    fun setOtherUserTyping(isTyping: Boolean) {
        _state.update { it.copy(otherUserTyping = isTyping) }
    }

    private fun observeWebSocketEvents() {
        // Collect new messages from WebSocket
        viewModelScope.launch {
            for (message in webSocketService.newMessages) {
                // Only add message if it belongs to this chat
                if (_state.value.isIndividualChat) {
                    val otherId = _state.value.otherUserId
                    if (otherId != null &&
                        ((message.senderId == currentUserId && message.receiverId == otherId) ||
                         (message.senderId == otherId && message.receiverId == currentUserId))) {
                        addNewMessage(message)
                    }
                } else {
                    val convId = conversationId?.toLongOrNull()
                    if (convId != null && message.conversationId == convId) {
                        addNewMessage(message)
                    }
                }
            }
        }
        // Collect typing status
        viewModelScope.launch {
            webSocketService.typingStatus.collect { event ->
                val otherId = _state.value.otherUserId
                if (event != null && otherId != null && event.senderId == otherId) {
                    setOtherUserTyping(event.isTyping)
                }
            }
        }
    }
} 