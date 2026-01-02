package com.devsneha.chatlib.network

import com.devsneha.chatlib.model.ApiResponse
import com.devsneha.chatlib.model.Chat
import com.devsneha.chatlib.model.CreateChatRequest
import com.devsneha.chatlib.model.MarkAsReadRequest
import com.devsneha.chatlib.model.Message
import com.devsneha.chatlib.model.PaginatedResponse
import com.devsneha.chatlib.model.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    
    // ============ Chat Endpoints ============
    
    @GET("api/v1/chats")
    suspend fun getChats(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("userId") userId: String
    ): PaginatedResponse<Chat>
    
    @GET("api/v1/chats/{chatId}")
    suspend fun getChatDetails(
        @Path("chatId") chatId: String
    ): ApiResponse<Chat>
    
    @POST("api/v1/chats")
    suspend fun createChat(
        @Body request: CreateChatRequest
    ): ApiResponse<Chat>
    
    @PATCH("api/v1/chats/{chatId}")
    suspend fun updateChat(
        @Path("chatId") chatId: String,
        @Body chat: Chat
    ): ApiResponse<Chat>
    
    @DELETE("api/v1/chats/{chatId}")
    suspend fun deleteChat(
        @Path("chatId") chatId: String
    ): ApiResponse<String>
    
    // ============ Message Endpoints ============
    
    @GET("api/v1/chats/{chatId}/messages")
    suspend fun getMessages(
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50,
        @Query("before") beforeTimestamp: Long? = null
    ): PaginatedResponse<Message>
    
    @POST("api/v1/chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: String,
        @Body request: SendMessageRequest
    ): ApiResponse<Message>
    
    @GET("api/v1/messages/{messageId}")
    suspend fun getMessage(
        @Path("messageId") messageId: String
    ): ApiResponse<Message>
    
    @PATCH("api/v1/messages/{messageId}")
    suspend fun updateMessage(
        @Path("messageId") messageId: String,
        @Body message: Message
    ): ApiResponse<Message>
    
    @DELETE("api/v1/messages/{messageId}")
    suspend fun deleteMessage(
        @Path("messageId") messageId: String
    ): ApiResponse<String>
    
    @POST("api/v1/messages/mark-as-read")
    suspend fun markMessagesAsRead(
        @Body request: MarkAsReadRequest
    ): ApiResponse<String>
    
    // ============ Search Endpoints ============
    
    @GET("api/v1/chats/search")
    suspend fun searchChats(
        @Query("query") query: String,
        @Query("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<Chat>
    
    @GET("api/v1/chats/{chatId}/messages/search")
    suspend fun searchMessages(
        @Path("chatId") chatId: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): PaginatedResponse<Message>
    
    // ============ Typing Indicator & Status Endpoints ============
    
    @POST("api/v1/chats/{chatId}/typing")
    suspend fun sendTypingIndicator(
        @Path("chatId") chatId: String,
        @Query("userId") userId: String,
        @Query("isTyping") isTyping: Boolean
    ): ApiResponse<String>
    
    @POST("api/v1/chats/{chatId}/user-status")
    suspend fun updateUserStatus(
        @Path("chatId") chatId: String,
        @Query("userId") userId: String,
        @Query("status") status: String // "online", "offline", "away"
    ): ApiResponse<String>
}
