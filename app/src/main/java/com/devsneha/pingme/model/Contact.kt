package com.devsneha.pingme.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val photoUri: String? = null,
    val email: String? = null,
    val isWhatsAppContact: Boolean = false,
    var connectionStatus: ConnectionStatus = ConnectionStatus.NOT_CONNECTED
)

enum class ConnectionStatus {
    NOT_CONNECTED,
    REQUEST_SENT,
    CONNECTED
}