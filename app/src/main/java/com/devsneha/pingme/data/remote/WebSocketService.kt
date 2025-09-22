package com.devsneha.pingme.data.remote

import com.devsneha.pingme.model.Message
import com.devsneha.pingme.model.WebSocketMessage
import com.devsneha.pingme.model.WebSocketDestinations
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.launch

@Singleton
class WebSocketService @Inject constructor() {
    
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _newMessages = Channel<Message>()
    val newMessages = _newMessages
    
    private val _typingStatus = MutableStateFlow<TypingEvent?>(null)
    val typingStatus: StateFlow<TypingEvent?> = _typingStatus.asStateFlow()
    
    private val _statusUpdates = MutableStateFlow<StatusEvent?>(null)
    val statusUpdates: StateFlow<StatusEvent?> = _statusUpdates.asStateFlow()
    
    fun connect(url: String, userId: Long) {
        val request = Request.Builder()
            .url("$url?userId=$userId")
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionState.value = ConnectionState.CONNECTED
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    handleWebSocketMessage(text)
                } catch (e: Exception) {
                    // Handle parsing error
                }
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _connectionState.value = ConnectionState.DISCONNECTED
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionState.value = ConnectionState.ERROR
            }
        })
    }
    
    @OptIn(DelicateCoroutinesApi::class)
    private fun handleWebSocketMessage(text: String) {
        try {
            // Try to parse as Message first (for chat messages)
            val message = gson.fromJson(text, Message::class.java)
            if (message.action == "SEND_MESSAGE") {
                GlobalScope.launch {
                    _newMessages.send(message)
                }
                return
            }
        } catch (e: Exception) {
            // Not a message, try other types
        }
        
        try {
            // Try to parse as WebSocketMessage for other events
            val wsMessage = gson.fromJson(text, WebSocketMessage::class.java)
            when (wsMessage.action) {
                "TYPING" -> {
                    _typingStatus.value = TypingEvent(
                        senderId = wsMessage.senderId,
                        receiverId = wsMessage.receiverId ?: 0,
                        isTyping = true
                    )
                }
                "STOP_TYPING" -> {
                    _typingStatus.value = TypingEvent(
                        senderId = wsMessage.senderId,
                        receiverId = wsMessage.receiverId ?: 0,
                        isTyping = false
                    )
                }
                "ONLINE" -> {
                    _statusUpdates.value = StatusEvent(
                        userId = wsMessage.senderId,
                        isOnline = true
                    )
                }
                "OFFLINE" -> {
                    _statusUpdates.value = StatusEvent(
                        userId = wsMessage.senderId,
                        isOnline = false
                    )
                }
            }
        } catch (e: Exception) {
            // Handle parsing error
        }
    }
    
    // Send individual message
    fun sendMessage(senderId: Long, receiverId: Long, content: String, messageType: String = "TEXT") {
        val wsMessage = WebSocketMessage(
            senderId = senderId,
            receiverId = receiverId,
            content = content,
            messageType = messageType,
            action = "SEND_MESSAGE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Send group message
    fun sendGroupMessage(senderId: Long, conversationId: Long, content: String, messageType: String = "TEXT") {
        val wsMessage = WebSocketMessage(
            senderId = senderId,
            conversationId = conversationId,
            content = content,
            messageType = messageType,
            action = "SEND_MESSAGE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Send typing indicator
    fun sendTypingIndicator(senderId: Long, receiverId: Long, isTyping: Boolean) {
        val wsMessage = WebSocketMessage(
            senderId = senderId,
            receiverId = receiverId,
            action = if (isTyping) "TYPING" else "STOP_TYPING"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Send online status
    fun sendOnlineStatus(userId: Long, isOnline: Boolean) {
        val wsMessage = WebSocketMessage(
            senderId = userId,
            action = if (isOnline) "ONLINE" else "OFFLINE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Mark messages as read
    fun markMessagesAsRead(senderId: Long, receiverId: Long) {
        val wsMessage = WebSocketMessage(
            senderId = senderId,
            receiverId = receiverId,
            action = "READ_MESSAGE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Add user to chat (online notification)
    fun addUserToChat(userId: Long, username: String) {
        val wsMessage = WebSocketMessage(
            senderId = userId,
            senderName = username,
            action = "ONLINE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    // Leave chat (offline notification)
    fun leaveChat(userId: Long, username: String) {
        val wsMessage = WebSocketMessage(
            senderId = userId,
            senderName = username,
            action = "OFFLINE"
        )
        val json = gson.toJson(wsMessage)
        webSocket?.send(json)
    }
    
    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
    
    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
    
    data class TypingEvent(
        val senderId: Long,
        val receiverId: Long,
        val isTyping: Boolean
    )
    
    data class StatusEvent(
        val userId: Long,
        val isOnline: Boolean
    )
} 