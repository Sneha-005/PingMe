package com.devsneha.pingme.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import android.widget.Toast
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.devsneha.pingme.utility.composeUtility.CompletePreviews
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(
    onBackClick: () -> Unit,
    onVerifySuccess: () -> Unit,
    phoneNumber: String = "",
    username: String = "",
    viewModel: OtpVerificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val focusRequesters = remember { List(6) { FocusRequester() } }
    val context = LocalContext.current

    // Set phone number and username when screen is first displayed
    LaunchedEffect(Unit) {
        if (phoneNumber.isNotEmpty()) {
            viewModel.setPhoneNumber(phoneNumber)
        }
        if (username.isNotEmpty()) {
            viewModel.setUsername(username)
        }
    }

    // Handle timer countdown
    LaunchedEffect(state.timeLeft) {
        if (state.timeLeft > 0) {
            delay(1000)
            viewModel.updateTimeLeft(state.timeLeft - 1)
        }
    }

    // Handle success and error states
    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null) {
            Log.d("OtpVerifyScreen", "OTP verification successful: ${state.successMessage}")
            Toast.makeText(context, state.successMessage, Toast.LENGTH_LONG).show()
            onVerifySuccess()
        }
        if (state.error != null) {
            Log.e("OtpVerifyScreen", "OTP verification error: ${state.error}")
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.primary
                    )
                }
                Text(
                    text = "Verify OTP",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OTP Description
            Text(
                text = "Enter the 6-digit code sent to your phone",
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "We've sent a verification code to ${state.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Show username being verified
            if (state.username.isNotEmpty()) {
                Text(
                    text = "Verifying account: ${state.username}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // OTP Input Boxes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0..5) {
                    val digit = if (i < state.otp.length) state.otp[i].toString() else ""
                    OutlinedTextField(
                        value = digit,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                if (newValue.isNotEmpty()) {
                                    val newOtp = state.otp.take(i) + newValue + state.otp.drop(i + 1)
                                    viewModel.updateOtp(newOtp)
                                    if (i < 5) {
                                        focusRequesters[i + 1].requestFocus()
                                    }
                                } else {
                                    val newOtp = state.otp.take(i) + state.otp.drop(i + 1)
                                    viewModel.updateOtp(newOtp)
                                    if (i > 0) {
                                        focusRequesters[i - 1].requestFocus()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .focusRequester(focusRequesters[i]),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.outline,
                            focusedLabelColor = colorScheme.primary,
                            cursorColor = colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // OTP Error Message
            if (state.otpError != null) {
                Text(
                    text = state.otpError!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Timer and Resend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (!state.canResend) {
                    Text(
                        text = "Resend code in ${state.timeLeft}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                } else {
                    TextButton(
                        onClick = { viewModel.resendOtp() }
                    ) {
                        Text(
                            text = "Resend Code",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.primary
                        )
                    }
                }
            }

            // Verify Button
            Button(
                onClick = { viewModel.verifyOtp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.otp.length == 6 && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    disabledContainerColor = colorScheme.primary.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Verify",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            if (state.isLoading) {
                Text(
                    text = "Verifying...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            // Show verification process info
            if (state.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Verification Process",
                            style = MaterialTheme.typography.titleSmall,
                            color = colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "• OTP Validation\n• Data Integrity Check\n• Security Verification\n• Database Validation\n• Account Creation",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@CompletePreviews
@Composable
fun OtpVerifyScreenPreview() {
    OtpVerifyScreen(
        onBackClick = {},
        onVerifySuccess = {}
    )
}