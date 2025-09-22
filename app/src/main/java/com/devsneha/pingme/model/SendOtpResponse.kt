package com.devsneha.pingme.model

data class SendOtpResponse(
    val message: String
) {
    fun isSuccess(): Boolean {
        return message.contains("success", ignoreCase = true) || 
               message.contains("otp", ignoreCase = true) ||
               message.contains("sent", ignoreCase = true)
    }
} 