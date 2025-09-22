package com.devsneha.pingme

import com.devsneha.pingme.utility.PhoneNumberValidator
import org.junit.Test
import org.junit.Assert.*

class PhoneNumberValidatorTest {

    @Test
    fun `valid phone numbers should pass validation`() {
        val validPhoneNumbers = listOf(
            "+1234567890",      // US format (10 digits)
            "+919876543210",    // India format (12 digits)
            "+447911123456",    // UK format (12 digits)
            "+61412345678",     // Australia format (11 digits)
            "+123456789012345"  // Maximum length (15 digits)
        )

        validPhoneNumbers.forEach { phoneNumber ->
            assertTrue("Phone number $phoneNumber should be valid", 
                PhoneNumberValidator.isValidPhoneNumber(phoneNumber))
        }
    }

    @Test
    fun `invalid phone numbers should fail validation`() {
        val invalidPhoneNumbers = listOf(
            "",                 // Empty
            "1234567890",       // Missing +
            "+",                // Only +
            "++1234567890",     // Double +
            "+01234567890",     // Starts with 0
            "+1234567890123456", // Too long (16 digits)
            "+123456789",       // Too short (9 digits)
            "+abc123456",       // Contains letters
            "+123-456-7890",    // Contains hyphens
            "+123 456 7890",    // Contains spaces
            "+12345678",        // Too short (8 digits)
            "+1234567890123456" // Too long (16 digits)
        )

        invalidPhoneNumbers.forEach { phoneNumber ->
            assertFalse("Phone number $phoneNumber should be invalid", 
                PhoneNumberValidator.isValidPhoneNumber(phoneNumber))
        }
    }

    @Test
    fun `error messages should be appropriate for invalid phone numbers`() {
        assertEquals("Phone number cannot be empty", 
            PhoneNumberValidator.getPhoneNumberErrorMessage(""))
        
        assertEquals("Phone number must start with +", 
            PhoneNumberValidator.getPhoneNumberErrorMessage("1234567890"))
        
        assertEquals("Phone number must contain only digits after the +", 
            PhoneNumberValidator.getPhoneNumberErrorMessage("+abc123"))
        
        assertEquals("Phone number is too short (minimum 10 digits)", 
            PhoneNumberValidator.getPhoneNumberErrorMessage("+123456789"))
        
        assertEquals("Phone number is too long (maximum 15 digits)", 
            PhoneNumberValidator.getPhoneNumberErrorMessage("+1234567890123456"))
    }

    @Test
    fun `valid phone numbers should return null error message`() {
        assertNull(PhoneNumberValidator.getPhoneNumberErrorMessage("+1234567890"))
        assertNull(PhoneNumberValidator.getPhoneNumberErrorMessage("+919876543210"))
        assertNull(PhoneNumberValidator.getPhoneNumberErrorMessage("+447911123456"))
    }
} 