package com.devsneha.chatlib.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object ChatShapes {
    // Message bubble shapes
    // Sent messages: very rounded on 3 corners, less rounded (pointed) on top-right
    val sentMessageBubble = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 4.dp,  // Pointed tail on top-right
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )
    // Received messages: very rounded on 3 corners, less rounded (pointed) on top-left
    val receivedMessageBubble = RoundedCornerShape(
        topStart = 4.dp,  // Pointed tail on top-left
        topEnd = 20.dp,
        bottomStart = 20.dp,
        bottomEnd = 20.dp
    )
    val messageBubble = RoundedCornerShape(20.dp)
    
    // Input shapes
    val inputField = RoundedCornerShape(24.dp)
    
    // Avatar shapes
    val avatar = RoundedCornerShape(50)
    
    // Quick reply shapes
    val quickReply = RoundedCornerShape(20.dp)
    
    // Separator shapes
    val separator = RoundedCornerShape(12.dp)
}