package com.devsneha.pingme.model

data class OtpVerificationResponse(
    val message: String,
    val token: String? = null
) 