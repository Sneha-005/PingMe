package com.devsneha.chatlib.ui.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.devsneha.chatlib.theme.ChatColors
import com.devsneha.chatlib.theme.ChatShapes

@Composable
fun ChatAvatar(
    name: String,
    avatarUrl: String? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ChatColors.avatarBackground,
    textColor: Color = ChatColors.avatarText
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null && avatarUrl.isNotEmpty()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar for $name",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            // Show initials if no avatar URL
            val initials = name
                .split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
            
            val displayText = if (initials.isNotEmpty()) {
                initials
            } else {
                name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
            }
            
            Text(
                text = displayText,
                color = textColor,
                fontSize = (size.value * 0.4).sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

