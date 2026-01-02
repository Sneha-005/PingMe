package com.devsneha.chatlib.repository

import com.devsneha.chatlib.model.Chat
import com.devsneha.chatlib.model.CreateChatRequest
import com.devsneha.chatlib.model.MarkAsReadRequest
import com.devsneha.chatlib.model.Message
import com.devsneha.chatlib.model.PaginatedResponse
import com.devsneha.chatlib.model.SendMessageRequest
import com.devsneha.chatlib.network.ChatApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception, val message: String = exception.message ?: "Unknown error") : Result<T>()
    class Loading<T> : Result<T>()
}

class ChatRepository @Inject constructor(
    private val apiService: ChatApiService
) {
    
    // ============ Chat Operations ============
    
    fun getChats(userId: String, page: Int = 1, pageSize: Int = 20): Flow<Result<PaginatedResponse<Chat>>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.getChats(page = page, pageSize = pageSize, userId = userId)
            emit(Result.Success(response))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error fetching chats: ${e.message}"))
        }
    }
    
    fun getChatDetails(chatId: String): Flow<Result<Chat>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.getChatDetails(chatId)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("No data received"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error fetching chat details: ${e.message}"))
        }
    }
    
    fun createChat(request: CreateChatRequest): Flow<Result<Chat>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.createChat(request)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("Failed to create chat"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error creating chat: ${e.message}"))
        }
    }
    
    fun updateChat(chatId: String, chat: Chat): Flow<Result<Chat>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.updateChat(chatId, chat)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("Failed to update chat"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error updating chat: ${e.message}"))
        }
    }
    
    fun deleteChat(chatId: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.deleteChat(chatId)
            if (response.success) {
                emit(Result.Success("Chat deleted successfully"))
            } else {
                emit(Result.Error(Exception("Failed to delete chat"), response.error ?: "Unknown error"))
            }
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error deleting chat: ${e.message}"))
        }
    }
    
    // ============ Message Operations ============
    
    fun getMessages(
        chatId: String,
        page: Int = 1,
        pageSize: Int = 50,
        beforeTimestamp: Long? = null
    ): Flow<Result<PaginatedResponse<Message>>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.getMessages(
                chatId = chatId,
                page = page,
                pageSize = pageSize,
                beforeTimestamp = beforeTimestamp
            )
            emit(Result.Success(response))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error fetching messages: ${e.message}"))
        }
    }
    
    fun sendMessage(chatId: String, request: SendMessageRequest): Flow<Result<Message>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.sendMessage(chatId, request)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("Failed to send message"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error sending message: ${e.message}"))
        }
    }
    
    fun getMessage(messageId: String): Flow<Result<Message>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.getMessage(messageId)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("No data received"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error fetching message: ${e.message}"))
        }
    }
    
    fun updateMessage(messageId: String, message: Message): Flow<Result<Message>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.updateMessage(messageId, message)
            response.data?.let {
                emit(Result.Success(it))
            } ?: emit(Result.Error(Exception("Failed to update message"), response.error ?: "Unknown error"))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error updating message: ${e.message}"))
        }
    }
    
    fun deleteMessage(messageId: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.deleteMessage(messageId)
            if (response.success) {
                emit(Result.Success("Message deleted successfully"))
            } else {
                emit(Result.Error(Exception("Failed to delete message"), response.error ?: "Unknown error"))
            }
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error deleting message: ${e.message}"))
        }
    }
    
    fun markMessagesAsRead(messageIds: List<String>): Flow<Result<String>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.markMessagesAsRead(MarkAsReadRequest(messageIds))
            if (response.success) {
                emit(Result.Success("Messages marked as read"))
            } else {
                emit(Result.Error(Exception("Failed to mark messages"), response.error ?: "Unknown error"))
            }
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error marking messages: ${e.message}"))
        }
    }
    
    // ============ Search Operations ============
    
    fun searchChats(
        query: String,
        userId: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<PaginatedResponse<Chat>>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.searchChats(query, userId, page, pageSize)
            emit(Result.Success(response))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error searching chats: ${e.message}"))
        }
    }
    
    fun searchMessages(
        chatId: String,
        query: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<Result<PaginatedResponse<Message>>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.searchMessages(chatId, query, page, pageSize)
            emit(Result.Success(response))
        } catch (e: IOException) {
            emit(Result.Error(e, "Network error occurred"))
        } catch (e: Exception) {
            emit(Result.Error(e, "Error searching messages: ${e.message}"))
        }
    }
    
    // ============ Status Operations ============
    
    fun sendTypingIndicator(chatId: String, userId: String, isTyping: Boolean): Flow<Result<String>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.sendTypingIndicator(chatId, userId, isTyping)
            if (response.success) {
                emit(Result.Success("Typing indicator sent"))
            } else {
                emit(Result.Error(Exception("Failed"), response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e, "Error sending typing indicator: ${e.message}"))
        }
    }
    
    fun updateUserStatus(chatId: String, userId: String, status: String): Flow<Result<String>> = flow {
        emit(Result.Loading())
        try {
            val response = apiService.updateUserStatus(chatId, userId, status)
            if (response.success) {
                emit(Result.Success("Status updated"))
            } else {
                emit(Result.Error(Exception("Failed"), response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e, "Error updating status: ${e.message}"))
        }
    }
}
