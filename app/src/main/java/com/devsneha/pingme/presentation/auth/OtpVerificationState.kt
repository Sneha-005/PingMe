package com.devsneha.pingme.presentation.auth

data class OtpVerificationState(
    val isLoading: Boolean = false,
    val otp: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val successMessage: String? = null,
    val error: String? = null,
    val otpError: String? = null,
    val timeLeft: Int = 30,
    val canResend: Boolean = false
) 