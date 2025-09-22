package com.devsneha.pingme.model

data class OtpVerificationRequest(
    val phoneNumber: String,
    val otp: String
) 