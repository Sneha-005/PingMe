package com.devsneha.pingme.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ApiServiceRepository
import com.devsneha.pingme.data.repository.UserRepository
import com.devsneha.pingme.model.SendOtpRequest
import com.devsneha.pingme.model.AuthUser
import com.devsneha.pingme.utility.PhoneNumberValidator
import com.devsneha.pingme.utility.UsernameValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SendOtpViewModel @Inject constructor(
    private val apiServiceRepository: ApiServiceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SendOtpState())
    val state: StateFlow<SendOtpState> = _state

    fun updateUsername(username: String) {
        _state.value = _state.value.copy(
            username = username,
            usernameError = null
        )
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _state.value = _state.value.copy(
            phoneNumber = phoneNumber,
            phoneNumberError = null
        )
    }

    fun sendOtp() {
        val currentState = _state.value
        val username = currentState.username.trim()
        val phoneNumber = currentState.phoneNumber.trim()

        // Clear previous errors
        _state.value = currentState.copy(
            error = null,
            successMessage = null,
            usernameError = null,
            phoneNumberError = null
        )

        // Validate username
        val usernameError = UsernameValidator.getUsernameErrorMessage(username)
        if (usernameError != null) {
            _state.value = currentState.copy(usernameError = usernameError)
            return
        }

        // Validate phone number
        val phoneError = PhoneNumberValidator.getPhoneNumberErrorMessage(phoneNumber)
        if (phoneError != null) {
            _state.value = currentState.copy(phoneNumberError = phoneError)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // --- OTP Skip Logic ---
            // Create a user and save it directly
            val user = AuthUser(username = username, phoneNumber = phoneNumber, token = "fake-token-for-dev")
            userRepository.saveUser(user)

            // Simulate a short delay and then set success
            delay(1000)

            _state.value = _state.value.copy(
                isLoading = false,
                successMessage = "Successfully registered (OTP Skipped).",
                error = null
            )
        }
    }

    fun clearErrors() {
        _state.value = _state.value.copy(
            error = null,
            successMessage = null,
            usernameError = null,
            phoneNumberError = null
        )
    }
} 