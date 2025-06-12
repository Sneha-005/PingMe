package com.devsneha.pingme.presentation

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
import com.devsneha.pingme.utility.composeUtility.CompletePreviews
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerifyScreen(
    onBackClick: () -> Unit,
    onVerifyClick: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(30) }
    var canResend by remember { mutableStateOf(false) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else {
            canResend = true
        }
    }

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
            text = "Enter the 6-digit code sent to your email",
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "We've sent a verification code to your email",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // OTP Input Boxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0..5) {
                val digit = if (i < otp.length) otp[i].toString() else ""
                OutlinedTextField(
                    value = digit,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1) {
                            if (newValue.isNotEmpty()) {
                                otp = otp.take(i) + newValue + otp.drop(i + 1)
                                if (i < 5) {
                                    focusRequesters[i + 1].requestFocus()
                                }
                            } else {
                                otp = otp.take(i) + otp.drop(i + 1)
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

        // Timer and Resend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (!canResend) {
                Text(
                    text = "Resend code in ${timeLeft}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onBackground.copy(alpha = 0.6f)
                )
            } else {
                TextButton(
                    onClick = {
                        // TODO: Implement resend OTP
                        timeLeft = 30
                        canResend = false
                    }
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
            onClick = { onVerifyClick(otp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = otp.length == 6,
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Verify",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@CompletePreviews
@Composable
fun OtpVerifyScreenPreview() {
    OtpVerifyScreen(
        onBackClick = {},
        onVerifyClick = {}
    )
}