package com.devsneha.pingme.utility

object UsernameValidator {
    
    private val restrictedKeywords = setOf("admin", "root", "system")
    
    /**
     * Validates username format according to the API specification
     * - 3-20 characters long
     * - Only letters (a-z, A-Z), numbers (0-9), and underscores (_)
     * - Cannot contain restricted keywords: "admin", "root", "system"
     */
    fun isValidUsername(username: String): Boolean {
        if (username.isEmpty()) return false
        
        // Check length (3-20 characters)
        if (username.length < 3 || username.length > 20) return false
        
        // Check if contains only allowed characters
        if (!username.all { it.isLetterOrDigit() || it == '_' }) return false
        
        // Check for restricted keywords (case-insensitive)
        val lowerUsername = username.lowercase()
        if (restrictedKeywords.any { lowerUsername.contains(it) }) return false
        
        return true
    }
    
    /**
     * Returns a user-friendly error message for invalid usernames
     */
    fun getUsernameErrorMessage(username: String): String? {
        return when {
            username.isEmpty() -> "Username cannot be empty"
            username.length < 3 -> "Username must be at least 3 characters long"
            username.length > 20 -> "Username must be 20 characters or less"
            !username.all { it.isLetterOrDigit() || it == '_' } -> "Username must contain only letters, numbers, and underscores"
            isRestrictedKeyword(username) -> "Username contains restricted keywords"
            else -> null
        }
    }
    
    /**
     * Checks if username contains restricted keywords
     */
    private fun isRestrictedKeyword(username: String): Boolean {
        val lowerUsername = username.lowercase()
        return restrictedKeywords.any { lowerUsername.contains(it) }
    }
    
    /**
     * Gets the list of restricted keywords
     */
    fun getRestrictedKeywords(): Set<String> {
        return restrictedKeywords.toSet()
    }
} 