package com.devsneha.pingme.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ChatRepository
import com.devsneha.pingme.data.repository.UserRepository
import com.devsneha.pingme.model.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ChatListState())
    val state: StateFlow<ChatListState> = _state.asStateFlow()
    
    private var currentUserId: Long = 1L // Default fallback
    private var currentUsername: String = ""

    init {
        setCurrentUser()
        loadConversations()
    }

    private fun setCurrentUser() {
        val user = userRepository.getUser()
        if (user != null) {
            currentUsername = user.username
            currentUserId = user.username.hashCode().toLong().absoluteValue // Fallback if no id
        }
    }
    
    fun loadConversations(page: Int = 0) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            chatRepository.getUserConversations(currentUserId, page)
                .onSuccess { paginatedResponse ->
                    _state.update { currentState ->
                        currentState.copy(
                            conversations = if (page == 0) {
                                paginatedResponse.content
                            } else {
                                currentState.conversations + paginatedResponse.content
                            },
                            isLoading = false,
                            currentPage = page,
                            hasMoreConversations = paginatedResponse.number < paginatedResponse.totalPages - 1
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            error = exception.message ?: "Failed to load conversations",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isNotEmpty()) {
            searchConversations(query)
        } else {
            loadConversations()
        }
    }
    
    private fun searchConversations(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true) }
            
            // For now, we'll filter locally. In a real app, you'd call an API
            val filteredConversations = _state.value.conversations.filter { conversation ->
                conversation.name.contains(query, ignoreCase = true) ||
                conversation.participants.any { participant ->
                    participant.username.contains(query, ignoreCase = true)
                }
            }
            
            _state.update { 
                it.copy(
                    conversations = filteredConversations,
                    isSearching = false
                )
            }
        }
    }
    
    fun loadMoreConversations() {
        if (!_state.value.hasMoreConversations || _state.value.isLoading) return
        
        val nextPage = _state.value.currentPage + 1
        loadConversations(nextPage)
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun refreshConversations() {
        loadConversations(0)
    }
} 