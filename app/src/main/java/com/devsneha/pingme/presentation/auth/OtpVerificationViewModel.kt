package com.devsneha.pingme.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ApiServiceRepository
import com.devsneha.pingme.data.repository.UserRepository
import com.devsneha.pingme.model.OtpVerificationRequest
import com.devsneha.pingme.model.SendOtpRequest
import com.devsneha.pingme.model.AuthUser
import com.devsneha.pingme.utility.OtpValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val apiServiceRepository: ApiServiceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OtpVerificationState())
    val state: StateFlow<OtpVerificationState> = _state

    fun updateOtp(otp: String) {
        _state.value = _state.value.copy(
            otp = otp,
            otpError = null
        )
    }

    fun setPhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(phoneNumber = phoneNumber)
    }

    fun setUsername(username: String) {
        _state.value = _state.value.copy(username = username)
    }

    fun updateTimeLeft(timeLeft: Int) {
        _state.value = _state.value.copy(
            timeLeft = timeLeft,
            canResend = timeLeft <= 0
        )
    }

    fun verifyOtp() {
        val currentState = _state.value
        val otp = currentState.otp.trim()
        val phoneNumber = currentState.phoneNumber.trim()

        // Clear previous errors
        _state.value = currentState.copy(
            error = null,
            successMessage = null,
            otpError = null
        )

        // Validate OTP using utility class
        val otpError = OtpValidator.getOtpErrorMessage(otp)
        if (otpError != null) {
            _state.value = currentState.copy(otpError = otpError)
            return
        }

        viewModelScope.launch {
            Log.d("OtpVerificationViewModel", "Attempting OTP verification: phone=$phoneNumber, otp=$otp")
            _state.value = _state.value.copy(isLoading = true)
            
            var retryCount = 0
            val maxRetries = 3
            
            while (retryCount < maxRetries) {
                try {
                    val response = apiServiceRepository.verifyOtp(phoneNumber, otp)

                    if (response.isSuccessful) {
                        response.body()?.let { otpResponse ->
                            Log.d("OtpVerificationViewModel", "OTP verification success: ${otpResponse.message}")
                            
                            // Save user data
                            val user = AuthUser(
                                username = currentState.username,
                                phoneNumber = phoneNumber,
                                token = otpResponse.token
                            )
                            userRepository.saveUser(user)

                            _state.value = _state.value.copy(
                                isLoading = false,
                                successMessage = otpResponse.message,
                                error = null
                            )
                            return@launch
                        } ?: run {
                            Log.e("OtpVerificationViewModel", "Empty response body")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = "Empty response body"
                            )
                            return@launch
                        }
                    } else {
                        // Handle error response with new error scenarios
                        val errorBody = response.errorBody()?.string()
                        Log.e("OtpVerificationViewModel", "OTP verification failed: code=${response.code()}, error=$errorBody")
                        
                        val errorMessage = when (response.code()) {
                            400 -> {
                                when {
                                    errorBody?.contains("Invalid OTP") == true -> "Invalid OTP"
                                    errorBody?.contains("No pending user found") == true -> "No pending user found for this phone number"
                                    errorBody?.contains("Username must be 3-20 characters") == true -> "Username must be 3-20 characters long and contain only letters, numbers, and underscores"
                                    errorBody?.contains("Username contains restricted keywords") == true -> "Username contains restricted keywords"
                                    else -> "OTP verification failed: ${response.code()}"
                                }
                            }
                            else -> "OTP verification failed: ${response.code()}"
                        }
                        
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        return@launch
                    }
                } catch (e: SocketTimeoutException) {
                    retryCount++
                    Log.w("OtpVerificationViewModel", "Timeout on attempt $retryCount: ${e.message}")
                    
                    if (retryCount >= maxRetries) {
                        Log.e("OtpVerificationViewModel", "Max retries reached for timeout")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Request timed out. Please check your internet connection and try again."
                        )
                        return@launch
                    } else {
                        // Wait before retrying
                        delay(2000L * retryCount) // Exponential backoff
                        continue
                    }
                } catch (e: UnknownHostException) {
                    Log.e("OtpVerificationViewModel", "Network connection error: ${e.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network and try again."
                    )
                    return@launch
                } catch (e: com.google.gson.stream.MalformedJsonException) {
                    Log.e("OtpVerificationViewModel", "JSON parsing error: ${e.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Server response format error. Please try again."
                    )
                    return@launch
                } catch (e: Exception) {
                    Log.e("OtpVerificationViewModel", "OTP verification exception: ${e.message}", e)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Network error: ${e.message}"
                    )
                    return@launch
                }
            }
        }
    }

    fun resendOtp() {
        val currentState = _state.value
        val username = currentState.username.trim()
        val phoneNumber = currentState.phoneNumber.trim()

        if (username.isEmpty() || phoneNumber.isEmpty()) {
            Log.e("OtpVerificationViewModel", "Cannot resend OTP: missing username or phone number")
            _state.value = currentState.copy(error = "Cannot resend OTP: missing user information")
            return
        }

        viewModelScope.launch {
            Log.d("OtpVerificationViewModel", "Resending OTP: username=$username, phone=$phoneNumber")
            _state.value = _state.value.copy(isLoading = true)
            
            var retryCount = 0
            val maxRetries = 3
            
            while (retryCount < maxRetries) {
                try {
                    val response = apiServiceRepository.sendOtp(username, phoneNumber)

                    if (response.isSuccessful) {
                        response.body()?.let { sendOtpResponse ->
                            Log.d("OtpVerificationViewModel", "Resend OTP success: ${sendOtpResponse.message}")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                successMessage = sendOtpResponse.message,
                                error = null,
                                timeLeft = 30,
                                canResend = false
                            )
                            return@launch
                        } ?: run {
                            Log.e("OtpVerificationViewModel", "Empty response body for resend")
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = "Empty response body"
                            )
                            return@launch
                        }
                    } else {
                        // Handle error response
                        val errorBody = response.errorBody()?.string()
                        Log.e("OtpVerificationViewModel", "Resend OTP failed: code=${response.code()}, error=$errorBody")
                        
                        val errorMessage = when (response.code()) {
                            400 -> {
                                when {
                                    errorBody?.contains("Username already exists") == true -> "Username already exists"
                                    errorBody?.contains("Phone number already registered") == true -> "Phone number already registered"
                                    errorBody?.contains("Phone number must be in international format") == true -> "Phone number must be in international format (e.g., +1234567890)"
                                    errorBody?.contains("Username must be 3-20 characters") == true -> "Username must be 3-20 characters long and contain only letters, numbers, and underscores"
                                    else -> "Resend OTP failed: ${response.code()}"
                                }
                            }
                            404 -> "Resend OTP endpoint not available. Please contact support."
                            500 -> "Server error. Please try again later."
                            else -> "Resend OTP failed: ${response.code()}"
                        }
                        
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                        return@launch
                    }
                } catch (e: SocketTimeoutException) {
                    retryCount++
                    Log.w("OtpVerificationViewModel", "Resend timeout on attempt $retryCount: ${e.message}")
                    
                    if (retryCount >= maxRetries) {
                        Log.e("OtpVerificationViewModel", "Max retries reached for resend timeout")
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = "Resend request timed out. Please check your internet connection and try again."
                        )
                        return@launch
                    } else {
                        // Wait before retrying
                        delay(2000L * retryCount) // Exponential backoff
                        continue
                    }
                } catch (e: UnknownHostException) {
                    Log.e("OtpVerificationViewModel", "Resend network connection error: ${e.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No internet connection. Please check your network and try again."
                    )
                    return@launch
                } catch (e: com.google.gson.stream.MalformedJsonException) {
                    Log.e("OtpVerificationViewModel", "Resend JSON parsing error: ${e.message}")
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Server response format error. Please try again."
                    )
                    return@launch
                } catch (e: Exception) {
                    Log.e("OtpVerificationViewModel", "Resend OTP exception: ${e.message}", e)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Network error: ${e.message}"
                    )
                    return@launch
                }
            }
        }
    }

    fun clearErrors() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null,
            otpError = null
        )
    }
} 