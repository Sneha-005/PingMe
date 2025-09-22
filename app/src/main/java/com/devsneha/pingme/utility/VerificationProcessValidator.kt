package com.devsneha.pingme.utility

object VerificationProcessValidator {
    
    /**
     * Represents the verification process steps as described in the API specification
     */
    enum class VerificationStep {
        OTP_VALIDATION,
        DATA_INTEGRITY,
        SECURITY_CHECKS,
        DATABASE_CHECKS,
        ACCOUNT_CREATION
    }
    
    /**
     * Validates the verification process and returns any issues found
     * This simulates the server-side validation process
     */
    fun validateVerificationProcess(
        username: String,
        phoneNumber: String,
        otp: String
    ): List<String> {
        val issues = mutableListOf<String>()
        
        // 1. OTP Validation
        if (!OtpValidator.isValidOtp(otp)) {
            issues.add("Invalid OTP")
        }
        
        // 2. Data Integrity
        val usernameError = UsernameValidator.getUsernameErrorMessage(username)
        if (usernameError != null) {
            issues.add("Verification failed: $usernameError")
        }
        
        val phoneError = PhoneNumberValidator.getPhoneNumberErrorMessage(phoneNumber)
        if (phoneError != null) {
            issues.add("Verification failed: Phone number validation failed")
        }
        
        // 3. Security Checks
        if (containsSuspiciousPatterns(username, phoneNumber)) {
            issues.add("Verification failed: Account creation blocked due to suspicious patterns. Please contact support.")
        }
        
        return issues
    }
    
    /**
     * Checks for suspicious patterns that might indicate abuse
     */
    private fun containsSuspiciousPatterns(username: String, phoneNumber: String): Boolean {
        // Check for repetitive patterns
        if (username.length >= 3) {
            val firstChar = username[0]
            if (username.all { it == firstChar }) {
                return true // All same characters
            }
        }
        
        // Check for sequential patterns
        if (username.length >= 3 && username.all { it.isDigit() }) {
            val digits = username.map { it.toString().toInt() }
            if (isSequential(digits)) {
                return true // Sequential digits
            }
        }
        
        // Check for phone number patterns
        if (phoneNumber.length >= 10) {
            val digits = phoneNumber.substring(1).map { it.toString().toInt() }
            if (isSequential(digits) || digits.all { it == digits[0] }) {
                return true // Sequential or repeated digits
            }
        }
        
        return false
    }
    
    /**
     * Checks if a list of integers is sequential
     */
    private fun isSequential(digits: List<Int>): Boolean {
        if (digits.size < 3) return false
        
        // Check ascending sequence
        var isAscending = true
        for (i in 1 until digits.size) {
            if (digits[i] != digits[i-1] + 1) {
                isAscending = false
                break
            }
        }
        
        // Check descending sequence
        var isDescending = true
        for (i in 1 until digits.size) {
            if (digits[i] != digits[i-1] - 1) {
                isDescending = false
                break
            }
        }
        
        return isAscending || isDescending
    }
    
    /**
     * Gets a user-friendly description of the verification process
     */
    fun getVerificationProcessDescription(): String {
        return """
            The verification process includes:
            1. OTP Validation - Verifies the provided OTP matches the stored OTP
            2. Data Integrity - Validates username and phone number format
            3. Security Checks - Checks for restricted keywords and suspicious patterns
            4. Database Checks - Ensures username and phone number are still available
            5. Account Creation - Saves verified user to database
        """.trimIndent()
    }
} 