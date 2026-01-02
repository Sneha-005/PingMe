package com.devsneha.chatlib.ui.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devsneha.chatlib.theme.ChatShapes
import com.devsneha.chatlib.theme.ChatTheme
import com.devsneha.chatlib.theme.ChatTypography
import com.devsneha.chatlib.theme.LocalChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    placeholder: String = "Type a message...",
    modifier: Modifier = Modifier,
    showAttachmentButton: Boolean = true,
    onAttachmentClick: () -> Unit = {},
    showMoreButton: Boolean = false,
    onMoreClick: () -> Unit = {},
    enabled: Boolean = true,
    theme: ChatTheme = LocalChatTheme.current
) {
    val canSend = enabled && value.isNotBlank()

    // Use send button gradient colors for the send button
    val sendButtonGradient = Brush.radialGradient(
        colors = listOf(
            theme.sendButtonGradientStart,
            theme.sendButtonGradientEnd
        ),
        radius = 100f
    )

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = ChatTypography.inputText,
                color = theme.inputPlaceholder
            )
        },
        shape = ChatShapes.inputField,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = theme.inputBackground,
            unfocusedContainerColor = theme.inputBackground,
            disabledContainerColor = theme.inputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = theme.inputText,
            focusedTextColor = theme.inputText,
            unfocusedTextColor = theme.inputText,
            disabledTextColor = theme.inputPlaceholder
        ),
        textStyle = ChatTypography.inputText,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        leadingIcon = if (showAttachmentButton) {
            {
                IconButton(
                    enabled = enabled,
                    onClick = onAttachmentClick
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else null,
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showMoreButton) {
                    IconButton(
                        enabled = enabled,
                        onClick = onMoreClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(44.dp)
                        .shadow(elevation = 6.dp, shape = CircleShape, clip = false)
                        .clip(CircleShape)
                        .background(brush = sendButtonGradient)
                        .clickable(enabled = canSend, onClick = onSend)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White.copy(alpha = if (canSend) 1f else 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    )
}