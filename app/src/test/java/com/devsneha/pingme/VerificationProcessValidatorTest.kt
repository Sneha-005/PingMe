package com.devsneha.pingme

import com.devsneha.pingme.utility.VerificationProcessValidator
import org.junit.Test
import org.junit.Assert.*

class VerificationProcessValidatorTest {

    @Test
    fun `valid verification process should pass all checks`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "john_doe",
            phoneNumber = "+1234567890",
            otp = "123456"
        )
        
        assertTrue("Valid verification should have no issues", issues.isEmpty())
    }

    @Test
    fun `invalid OTP should be detected`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "john_doe",
            phoneNumber = "+1234567890",
            otp = "12345" // Too short
        )
        
        assertTrue("Should detect invalid OTP", issues.any { it.contains("Invalid OTP") })
    }

    @Test
    fun `invalid username should be detected`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "ab", // Too short
            phoneNumber = "+1234567890",
            otp = "123456"
        )
        
        assertTrue("Should detect invalid username", issues.any { it.contains("Username must be at least 3 characters") })
    }

    @Test
    fun `invalid phone number should be detected`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "john_doe",
            phoneNumber = "1234567890", // Missing +
            otp = "123456"
        )
        
        assertTrue("Should detect invalid phone number", issues.any { it.contains("Phone number validation failed") })
    }

    @Test
    fun `suspicious patterns should be detected`() {
        // Test repetitive username
        val issues1 = VerificationProcessValidator.validateVerificationProcess(
            username = "aaa",
            phoneNumber = "+1234567890",
            otp = "123456"
        )
        
        assertTrue("Should detect repetitive username", issues1.any { it.contains("suspicious patterns") })
        
        // Test sequential username
        val issues2 = VerificationProcessValidator.validateVerificationProcess(
            username = "123",
            phoneNumber = "+1234567890",
            otp = "123456"
        )
        
        assertTrue("Should detect sequential username", issues2.any { it.contains("suspicious patterns") })
    }

    @Test
    fun `restricted keywords should be detected`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "admin",
            phoneNumber = "+1234567890",
            otp = "123456"
        )
        
        assertTrue("Should detect restricted keywords", issues.any { it.contains("restricted keywords") })
    }

    @Test
    fun `multiple issues should be detected simultaneously`() {
        val issues = VerificationProcessValidator.validateVerificationProcess(
            username = "ab", // Too short
            phoneNumber = "1234567890", // Missing +
            otp = "12345" // Too short
        )
        
        assertTrue("Should detect multiple issues", issues.size >= 3)
        assertTrue("Should detect invalid OTP", issues.any { it.contains("Invalid OTP") })
        assertTrue("Should detect invalid username", issues.any { it.contains("Username must be at least 3 characters") })
        assertTrue("Should detect invalid phone number", issues.any { it.contains("Phone number validation failed") })
    }

    @Test
    fun `verification process description should be available`() {
        val description = VerificationProcessValidator.getVerificationProcessDescription()
        
        assertTrue("Description should contain OTP validation", description.contains("OTP Validation"))
        assertTrue("Description should contain data integrity", description.contains("Data Integrity"))
        assertTrue("Description should contain security checks", description.contains("Security Checks"))
        assertTrue("Description should contain database checks", description.contains("Database Checks"))
        assertTrue("Description should contain account creation", description.contains("Account Creation"))
    }

    @Test
    fun `verification steps enum should have correct values`() {
        val steps = VerificationProcessValidator.VerificationStep.values()
        
        assertEquals("Should have 5 verification steps", 5, steps.size)
        assertTrue("Should contain OTP_VALIDATION", steps.any { it.name == "OTP_VALIDATION" })
        assertTrue("Should contain DATA_INTEGRITY", steps.any { it.name == "DATA_INTEGRITY" })
        assertTrue("Should contain SECURITY_CHECKS", steps.any { it.name == "SECURITY_CHECKS" })
        assertTrue("Should contain DATABASE_CHECKS", steps.any { it.name == "DATABASE_CHECKS" })
        assertTrue("Should contain ACCOUNT_CREATION", steps.any { it.name == "ACCOUNT_CREATION" })
    }
} 