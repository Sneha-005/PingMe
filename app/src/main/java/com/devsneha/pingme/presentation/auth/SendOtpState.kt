package com.devsneha.pingme.presentation.auth

data class SendOtpState(
    val isLoading: Boolean = false,
    val username: String = "",
    val phoneNumber: String = "",
    val successMessage: String? = null,
    val error: String? = null,
    val usernameError: String? = null,
    val phoneNumberError: String? = null
) 