package com.devsneha.chatlib.util

object Constants {
    // Base URL - Configure this with your backend API
    const val BASE_URL = "https://api.yourserver.com/"
    
    // API Endpoints
    const val CHATS_ENDPOINT = "api/v1/chats"
    const val MESSAGES_ENDPOINT = "api/v1/messages"
    
    // Pagination defaults
    const val DEFAULT_PAGE_SIZE = 20
    const val DEFAULT_MESSAGE_PAGE_SIZE = 50
    
    // Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    // WebSocket
    const val WS_BASE_URL = "wss://api.yourserver.com/"
}
