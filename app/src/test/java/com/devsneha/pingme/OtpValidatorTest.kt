package com.devsneha.pingme

import com.devsneha.pingme.utility.OtpValidator
import org.junit.Test
import org.junit.Assert.*

class OtpValidatorTest {

    @Test
    fun `valid OTP should pass validation`() {
        val validOtps = listOf(
            "123456",
            "000000",
            "999999",
            "123456"
        )

        validOtps.forEach { otp ->
            assertTrue("OTP $otp should be valid", 
                OtpValidator.isValidOtp(otp))
        }
    }

    @Test
    fun `invalid OTP should fail validation`() {
        val invalidOtps = listOf(
            "",           // Empty
            "12345",      // Too short
            "1234567",    // Too long
            "12345a",     // Contains letter
            "123 456",    // Contains space
            "123-456",    // Contains hyphen
            "abc123",     // Contains letters
            "12 34 56"    // Contains spaces
        )

        invalidOtps.forEach { otp ->
            assertFalse("OTP $otp should be invalid", 
                OtpValidator.isValidOtp(otp))
        }
    }

    @Test
    fun `error messages should be appropriate for invalid OTP`() {
        assertEquals("OTP cannot be empty", 
            OtpValidator.getOtpErrorMessage(""))
        
        assertEquals("OTP must be 6 digits", 
            OtpValidator.getOtpErrorMessage("12345"))
        
        assertEquals("OTP must be 6 digits", 
            OtpValidator.getOtpErrorMessage("1234567"))
        
        assertEquals("OTP must contain only digits", 
            OtpValidator.getOtpErrorMessage("12345a"))
        
        assertEquals("OTP must contain only digits", 
            OtpValidator.getOtpErrorMessage("123 456"))
    }

    @Test
    fun `valid OTP should return null error message`() {
        assertNull(OtpValidator.getOtpErrorMessage("123456"))
        assertNull(OtpValidator.getOtpErrorMessage("000000"))
        assertNull(OtpValidator.getOtpErrorMessage("999999"))
    }

    @Test
    fun `formatOtp should add spaces every 3 digits`() {
        assertEquals("123 456", OtpValidator.formatOtp("123456"))
        assertEquals("000 000", OtpValidator.formatOtp("000000"))
        assertEquals("999 999", OtpValidator.formatOtp("999999"))
    }

    @Test
    fun `cleanOtp should remove non-digit characters`() {
        assertEquals("123456", OtpValidator.cleanOtp("123 456"))
        assertEquals("123456", OtpValidator.cleanOtp("123-456"))
        assertEquals("123456", OtpValidator.cleanOtp("123abc456"))
        assertEquals("123456", OtpValidator.cleanOtp("123456"))
    }
} 