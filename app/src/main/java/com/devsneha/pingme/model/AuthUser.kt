package com.devsneha.pingme.model

data class AuthUser(
    val username: String,
    val phoneNumber: String,
    val token: String? = null
)