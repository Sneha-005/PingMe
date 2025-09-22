package com.devsneha.pingme.data.remote

import com.devsneha.pingme.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/users/send-otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<String>
    
    @FormUrlEncoded
    @POST("api/users/verify-otp")
    suspend fun verifyOtp(
        @Field("phoneNumber") phoneNumber: String,
        @Field("otp") otp: String
    ): Response<String>
    
    @POST("api/chat/send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): Response<Message>
    
    @GET("api/chat/messages/{user1Id}/{user2Id}")
    suspend fun getMessagesBetweenUsers(
        @Path("user1Id") user1Id: Long,
        @Path("user2Id") user2Id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<List<Message>>
    
    @POST("api/chat/group/send")
    suspend fun sendGroupMessage(
        @Body request: SendGroupMessageRequest
    ): Response<Message>
    
    @GET("api/chat/conversation/{conversationId}/messages")
    suspend fun getConversationMessages(
        @Path("conversationId") conversationId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<PaginatedResponse<Message>>
    
    @GET("api/chat/conversations/{userId}")
    suspend fun getUserConversations(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PaginatedResponse<Conversation>>
    
    @POST("api/chat/group/create")
    suspend fun createGroupConversation(
        @Query("name") name: String,
        @Query("description") description: String? = null,
        @Query("creatorId") creatorId: Long,
        @Body participantIds: List<Long>
    ): Response<Conversation>
    
    @POST("api/chat/group/{conversationId}/add-participant")
    suspend fun addParticipantToGroup(
        @Path("conversationId") conversationId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>
    
    @DELETE("api/chat/group/{conversationId}/remove-participant")
    suspend fun removeParticipantFromGroup(
        @Path("conversationId") conversationId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>
    
    @POST("api/chat/read")
    suspend fun markMessagesAsRead(
        @Query("userId") userId: Long,
        @Query("senderId") senderId: Long
    ): Response<Unit>
    
    @GET("api/chat/unread-count/{userId}")
    suspend fun getUnreadMessageCount(
        @Path("userId") userId: Long
    ): Response<Int>
    
    @POST("api/chat/typing")
    suspend fun sendTypingIndicator(
        @Query("senderId") senderId: Long,
        @Query("receiverId") receiverId: Long,
        @Query("isTyping") isTyping: Boolean
    ): Response<Unit>
    
    @POST("api/chat/status")
    suspend fun sendOnlineStatus(
        @Query("userId") userId: Long,
        @Query("isOnline") isOnline: Boolean
    ): Response<Unit>
    
    @GET("api/users/search")
    suspend fun searchUsers(
        @Query("query") query: String
    ): Response<List<User>>
    
    @GET("api/users/{userId}")
    suspend fun getUserProfile(
        @Path("userId") userId: Long
    ): Response<User>
    
    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body user: User
    ): Response<User>
}