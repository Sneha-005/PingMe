package com.devsneha.pingme.model

data class SendOtpRequest(
    val username: String,
    val phoneNumber: String
)