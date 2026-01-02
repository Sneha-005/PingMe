package com.devsneha.chatlib.ui.voice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.alpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.LocalChatTheme
import kotlinx.coroutines.delay

@Composable
fun VoiceNoteSheet(
    onCancel: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    theme: ChatTheme = LocalChatTheme.current
) {
    var isRecording by remember { mutableStateOf(true) }
    var seconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000)
            seconds += 1
        }
    }

    val titleColor = MaterialTheme.colorScheme.onSurface
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    val primaryBrush = Brush.linearGradient(
        colors = listOf(theme.sendButtonGradientStart, theme.sendButtonGradientEnd),
        start = Offset(0f, 0f),
        end = Offset(0f, 200f)
    )

    Surface(
        shape = sheetShape,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White.copy(alpha = 0.98f), shape = sheetShape)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Voice Note",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Waveform(brush = primaryBrush, isRecording = isRecording)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatTime(seconds),
                color = Color(0xFF9AA4B2),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable {
                            isRecording = false
                            onCancel()
                        }
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Cancel", tint = Color(0xFF6B7280))
                }

                // Record/Pause big button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(68.dp)
                        .shadow(10.dp, CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(brush = primaryBrush)
                        .clickable { isRecording = !isRecording }
                ) {
                    Icon(imageVector = Icons.Filled.Mic, contentDescription = "Record", tint = Color.White)
                }

                // Send
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.95f))
                        .clickable {
                            isRecording = false
                            onSend()
                        }
                ) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Send", tint = Color(0xFF6B7280))
                }
            }
        }
    }
}

@Composable
private fun Waveform(brush: Brush, isRecording: Boolean, modifier: Modifier = Modifier) {
    val barHeights = remember { (1..24).map { mutableStateOf((10..36).random().toFloat()) } }
    val alpha by animateFloatAsState(targetValue = if (isRecording) 1f else 0.6f, label = "wave_alpha")

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(150)
            barHeights.forEach { state ->
                state.value = (10..36).random().toFloat()
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        barHeights.forEachIndexed { index, heightState ->
            val barAlpha = if (index % 2 == 0) alpha else alpha * 0.8f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(heightState.value.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(brush)
                    .alpha(barAlpha)
            )
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}


