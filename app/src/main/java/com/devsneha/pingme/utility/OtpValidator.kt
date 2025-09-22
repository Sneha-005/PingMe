package com.devsneha.pingme.utility

object OtpValidator {
    
    /**
     * Validates OTP format
     * - Must be exactly 6 digits
     * - Must contain only numeric characters
     */
    fun isValidOtp(otp: String): Boolean {
        if (otp.isEmpty()) return false
        if (otp.length != 6) return false
        return otp.all { it.isDigit() }
    }
    
    /**
     * Returns a user-friendly error message for invalid OTP
     */
    fun getOtpErrorMessage(otp: String): String? {
        return when {
            otp.isEmpty() -> "OTP cannot be empty"
            otp.length != 6 -> "OTP must be 6 digits"
            !otp.all { it.isDigit() } -> "OTP must contain only digits"
            else -> null
        }
    }
    
    /**
     * Formats OTP for display (adds spaces every 3 digits)
     */
    fun formatOtp(otp: String): String {
        return otp.chunked(3).joinToString(" ")
    }
    
    /**
     * Removes formatting from OTP (removes spaces and other non-digit characters)
     */
    fun cleanOtp(otp: String): String {
        return otp.filter { it.isDigit() }
    }
} 