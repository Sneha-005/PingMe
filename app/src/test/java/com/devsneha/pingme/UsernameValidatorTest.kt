package com.devsneha.pingme

import com.devsneha.pingme.utility.UsernameValidator
import org.junit.Test
import org.junit.Assert.*

class UsernameValidatorTest {

    @Test
    fun `valid usernames should pass validation`() {
        val validUsernames = listOf(
            "john_doe",      // Letters and underscore
            "user123",       // Letters and numbers
            "test_user_123", // Letters, underscore, and numbers
            "abc",           // Minimum length
            "a".repeat(20),  // Maximum length
            "User123",       // Mixed case
            "user_name",     // Multiple underscores
            "123user",       // Numbers and letters
            "user_123_test"  // Complex valid username
        )

        validUsernames.forEach { username ->
            assertTrue("Username $username should be valid", 
                UsernameValidator.isValidUsername(username))
        }
    }

    @Test
    fun `invalid usernames should fail validation`() {
        val invalidUsernames = listOf(
            "",                    // Empty
            "ab",                  // Too short
            "a".repeat(21),        // Too long
            "user-name",           // Contains hyphen
            "user name",           // Contains space
            "user@name",           // Contains special character
            "user.name",           // Contains dot
            "admin",               // Restricted keyword
            "root",                // Restricted keyword
            "system",              // Restricted keyword
            "Admin",               // Restricted keyword (case insensitive)
            "ROOT",                // Restricted keyword (case insensitive)
            "System",              // Restricted keyword (case insensitive)
            "myadmin",             // Contains restricted keyword
            "rootuser",            // Contains restricted keyword
            "system123",           // Contains restricted keyword
            "user_admin",          // Contains restricted keyword
            "test_root_user",      // Contains restricted keyword
            "my_system_test"       // Contains restricted keyword
        )

        invalidUsernames.forEach { username ->
            assertFalse("Username $username should be invalid", 
                UsernameValidator.isValidUsername(username))
        }
    }

    @Test
    fun `error messages should be appropriate for invalid usernames`() {
        assertEquals("Username cannot be empty", 
            UsernameValidator.getUsernameErrorMessage(""))
        
        assertEquals("Username must be at least 3 characters long", 
            UsernameValidator.getUsernameErrorMessage("ab"))
        
        assertEquals("Username must be 20 characters or less", 
            UsernameValidator.getUsernameErrorMessage("a".repeat(21)))
        
        assertEquals("Username must contain only letters, numbers, and underscores", 
            UsernameValidator.getUsernameErrorMessage("user-name"))
        
        assertEquals("Username must contain only letters, numbers, and underscores", 
            UsernameValidator.getUsernameErrorMessage("user name"))
        
        assertEquals("Username contains restricted keywords", 
            UsernameValidator.getUsernameErrorMessage("admin"))
        
        assertEquals("Username contains restricted keywords", 
            UsernameValidator.getUsernameErrorMessage("myadmin"))
    }

    @Test
    fun `valid usernames should return null error message`() {
        assertNull(UsernameValidator.getUsernameErrorMessage("john_doe"))
        assertNull(UsernameValidator.getUsernameErrorMessage("user123"))
        assertNull(UsernameValidator.getUsernameErrorMessage("test_user_123"))
        assertNull(UsernameValidator.getUsernameErrorMessage("abc"))
        assertNull(UsernameValidator.getUsernameErrorMessage("a".repeat(20)))
    }

    @Test
    fun `restricted keywords should be accessible`() {
        val restrictedKeywords = UsernameValidator.getRestrictedKeywords()
        assertEquals(3, restrictedKeywords.size)
        assertTrue(restrictedKeywords.contains("admin"))
        assertTrue(restrictedKeywords.contains("root"))
        assertTrue(restrictedKeywords.contains("system"))
    }
} 