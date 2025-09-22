package com.devsneha.pingme.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import android.widget.Toast
import android.util.Log
import androidx.compose.ui.platform.LocalContext

@Composable
fun RegisterScreen(
    onNavigateToChatList: () -> Unit,
    viewModel: SendOtpViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    LaunchedEffect(state.successMessage, state.error) {
        if (state.successMessage != null) {
            Log.d("RegisterScreen", "Registration successful: ${state.successMessage}")
            Toast.makeText(context, state.successMessage, Toast.LENGTH_LONG).show()
            onNavigateToChatList()
        }
        if (state.error != null) {
            Log.e("RegisterScreen", "Registration error: ${state.error}")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create an Account",
                style = MaterialTheme.typography.headlineLarge,
                color = colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.updateUsername(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.usernameError != null,
                supportingText = {
                    if (state.usernameError != null) {
                        Text(
                            text = state.usernameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.updatePhoneNumber(it) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                isError = state.phoneNumberError != null,
                supportingText = {
                    if (state.phoneNumberError != null) {
                        Text(
                            text = state.phoneNumberError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text("Enter phone number in international format (e.g., +1234567890)")
                    }
                }
            )
            
            Button(
                onClick = {
                    Log.d("RegisterScreen", "Register clicked: username=${state.username}, phone=${state.phoneNumber}")
                    viewModel.sendOtp()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Register")
                }
            }
            
            if (state.isLoading) {
                Text(
                    text = "Registering...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}