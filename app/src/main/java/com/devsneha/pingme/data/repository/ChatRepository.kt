package com.devsneha.pingme.data.repository

import com.devsneha.pingme.data.remote.ApiService
import com.devsneha.pingme.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    // Individual messages
    suspend fun sendMessage(
        senderId: Long,
        receiverId: Long,
        content: String,
        messageType: String = "TEXT"
    ): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val request = SendMessageRequest(senderId, receiverId, content, messageType)
            val response = apiService.sendMessage(request)
            if (response.isSuccessful) {
                val message = response.body()
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("Failed to send message"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMessagesBetweenUsers(
        user1Id: Long,
        user2Id: Long,
        page: Int = 0,
        size: Int = 50
    ): Result<List<Message>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMessagesBetweenUsers(user1Id, user2Id, page, size)
            if (response.isSuccessful) {
                val messages = response.body()
                if (messages != null) {
                    Result.success(messages)
                } else {
                    Result.failure(Exception("Failed to get messages"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Group messages
    suspend fun sendGroupMessage(
        senderId: Long,
        conversationId: Long,
        content: String,
        messageType: String = "TEXT"
    ): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val request = SendGroupMessageRequest(senderId, conversationId, content, messageType)
            val response = apiService.sendGroupMessage(request)
            if (response.isSuccessful) {
                val message = response.body()
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("Failed to send group message"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getConversationMessages(
        conversationId: Long,
        page: Int = 0,
        size: Int = 50
    ): Result<PaginatedResponse<Message>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getConversationMessages(conversationId, page, size)
            if (response.isSuccessful) {
                val messages = response.body()
                if (messages != null) {
                    Result.success(messages)
                } else {
                    Result.failure(Exception("Failed to get conversation messages"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Conversations
    suspend fun getUserConversations(
        userId: Long,
        page: Int = 0,
        size: Int = 20
    ): Result<PaginatedResponse<Conversation>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserConversations(userId, page, size)
            if (response.isSuccessful) {
                val conversations = response.body()
                if (conversations != null) {
                    Result.success(conversations)
                } else {
                    Result.failure(Exception("Failed to get conversations"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createGroupConversation(
        name: String,
        description: String?,
        creatorId: Long,
        participantIds: List<Long>
    ): Result<Conversation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createGroupConversation(name, description, creatorId, participantIds)
            if (response.isSuccessful) {
                val conversation = response.body()
                if (conversation != null) {
                    Result.success(conversation)
                } else {
                    Result.failure(Exception("Failed to create group conversation"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addParticipantToGroup(
        conversationId: Long,
        userId: Long
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addParticipantToGroup(conversationId, userId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeParticipantFromGroup(
        conversationId: Long,
        userId: Long
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeParticipantFromGroup(conversationId, userId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Message status
    suspend fun markMessagesAsRead(
        userId: Long,
        senderId: Long
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.markMessagesAsRead(userId, senderId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUnreadMessageCount(userId: Long): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUnreadMessageCount(userId)
            if (response.isSuccessful) {
                val count = response.body()
                if (count != null) {
                    Result.success(count)
                } else {
                    Result.failure(Exception("Failed to get unread count"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Typing and status indicators
    suspend fun sendTypingIndicator(
        senderId: Long,
        receiverId: Long,
        isTyping: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendTypingIndicator(senderId, receiverId, isTyping)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendOnlineStatus(
        userId: Long,
        isOnline: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendOnlineStatus(userId, isOnline)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // User management
    suspend fun searchUsers(query: String): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchUsers(query)
            if (response.isSuccessful) {
                val users = response.body()
                if (users != null) {
                    Result.success(users)
                } else {
                    Result.failure(Exception("Failed to search users"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(userId: Long): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserProfile(userId)
            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to get user profile"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateProfile(user)
            if (response.isSuccessful) {
                val updatedUser = response.body()
                if (updatedUser != null) {
                    Result.success(updatedUser)
                } else {
                    Result.failure(Exception("Failed to update profile"))
                }
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 