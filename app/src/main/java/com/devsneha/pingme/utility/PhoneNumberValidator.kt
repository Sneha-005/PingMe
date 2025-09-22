package com.devsneha.pingme.utility

object PhoneNumberValidator {
    
    /**
     * Validates phone number format according to the API specification
     * - Must start with `+` followed by country code
     * - Country code: 1-3 digits (e.g., +1 for US, +91 for India)
     * - Total length: 10-15 digits including the `+`
     * - Examples: `+1234567890`, `+919876543210`, `+447911123456`
     */
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber.isEmpty()) return false
        
        // Must start with +
        if (!phoneNumber.startsWith("+")) return false
        
        // Remove the + and check if the rest contains only digits
        val digitsOnly = phoneNumber.substring(1)
        if (!digitsOnly.all { it.isDigit() }) return false
        
        // Total length should be 10-15 digits including the +
        if (phoneNumber.length < 11 || phoneNumber.length > 16) return false
        
        // More precise validation using regex
        val phoneRegex = Regex("^\\+[1-9]\\d{9,14}$")
        return phoneRegex.matches(phoneNumber)
    }
    
    /**
     * Returns a user-friendly error message for invalid phone numbers
     */
    fun getPhoneNumberErrorMessage(phoneNumber: String): String? {
        return when {
            phoneNumber.isEmpty() -> "Phone number cannot be empty"
            !phoneNumber.startsWith("+") -> "Phone number must start with +"
            !phoneNumber.substring(1).all { it.isDigit() } -> "Phone number must contain only digits after the +"
            phoneNumber.length < 11 -> "Phone number is too short (minimum 10 digits)"
            phoneNumber.length > 16 -> "Phone number is too long (maximum 15 digits)"
            !isValidPhoneNumber(phoneNumber) -> "Phone number must be in international format (e.g., +1234567890)"
            else -> null
        }
    }
} 