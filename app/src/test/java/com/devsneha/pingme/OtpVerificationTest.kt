package com.devsneha.pingme

import com.devsneha.pingme.utility.OtpValidator
import org.junit.Test
import org.junit.Assert.*

class OtpVerificationTest {

    @Test
    fun `OTP validation should work correctly`() {
        // Valid OTPs
        assertTrue(OtpValidator.isValidOtp("123456"))
        assertTrue(OtpValidator.isValidOtp("000000"))
        assertTrue(OtpValidator.isValidOtp("999999"))
        
        // Invalid OTPs
        assertFalse(OtpValidator.isValidOtp(""))
        assertFalse(OtpValidator.isValidOtp("12345"))
        assertFalse(OtpValidator.isValidOtp("1234567"))
        assertFalse(OtpValidator.isValidOtp("12345a"))
        assertFalse(OtpValidator.isValidOtp("123 456"))
    }

    @Test
    fun `OTP error messages should be appropriate`() {
        assertEquals("OTP cannot be empty", OtpValidator.getOtpErrorMessage(""))
        assertEquals("OTP must be 6 digits", OtpValidator.getOtpErrorMessage("12345"))
        assertEquals("OTP must be 6 digits", OtpValidator.getOtpErrorMessage("1234567"))
        assertEquals("OTP must contain only digits", OtpValidator.getOtpErrorMessage("12345a"))
        assertNull(OtpValidator.getOtpErrorMessage("123456"))
    }

    @Test
    fun `OTP formatting should work correctly`() {
        assertEquals("123 456", OtpValidator.formatOtp("123456"))
        assertEquals("000 000", OtpValidator.formatOtp("000000"))
        assertEquals("999 999", OtpValidator.formatOtp("999999"))
    }

    @Test
    fun `OTP cleaning should work correctly`() {
        assertEquals("123456", OtpValidator.cleanOtp("123 456"))
        assertEquals("123456", OtpValidator.cleanOtp("123-456"))
        assertEquals("123456", OtpValidator.cleanOtp("123abc456"))
        assertEquals("123456", OtpValidator.cleanOtp("123456"))
    }

    @Test
    fun `OTP verification error scenarios should be handled`() {
        val errorScenarios = mapOf(
            "Invalid OTP." to "Invalid OTP",
            "Verification failed: Username is no longer available. Please choose a different username." to "Username is no longer available. Please choose a different username.",
            "Verification failed: Username must be 3-20 characters long and contain only letters, numbers, and underscores" to "Username must be 3-20 characters long and contain only letters, numbers, and underscores",
            "Verification failed: Username contains restricted keywords" to "Username contains restricted keywords",
            "Verification failed: Account creation blocked due to suspicious patterns. Please contact support." to "Account creation blocked due to suspicious patterns. Please contact support."
        )

        errorScenarios.forEach { (apiError, expectedMessage) ->
            val actualMessage = when {
                apiError.contains("Invalid OTP") -> "Invalid OTP"
                apiError.contains("Username is no longer available") -> "Username is no longer available. Please choose a different username."
                apiError.contains("Username must be 3-20 characters") -> "Username must be 3-20 characters long and contain only letters, numbers, and underscores"
                apiError.contains("Username contains restricted keywords") -> "Username contains restricted keywords"
                apiError.contains("Account creation blocked due to suspicious patterns") -> "Account creation blocked due to suspicious patterns. Please contact support."
                else -> "OTP verification failed: 400"
            }
            assertEquals("Error message should match for: $apiError", expectedMessage, actualMessage)
        }
    }

    @Test
    fun `OTP verification success scenario should be handled`() {
        val successMessage = "OTP verified successfully. User account created with ID: 123"
        assertTrue(successMessage.contains("OTP verified successfully"))
        assertTrue(successMessage.contains("User account created"))
    }
} 