package com.devsneha.pingme.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ChatRepository
import com.devsneha.pingme.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewChatState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(NewChatState())
    val state: StateFlow<NewChatState> = _state.asStateFlow()
    
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isNotEmpty()) {
            searchUsers(query)
        } else {
            _state.update { it.copy(users = emptyList()) }
        }
    }
    
    private fun searchUsers(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            chatRepository.searchUsers(query)
                .onSuccess { users ->
                    _state.update { 
                        it.copy(
                            users = users,
                            isLoading = false
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { 
                        it.copy(
                            error = exception.message ?: "Failed to search users",
                            isLoading = false
                        )
                    }
                }
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
} 