package com.devsneha.chatlib.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ChatTypography {
    val messageText = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    )
    
    val messageTimestamp = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal
    )
    
    val senderName = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    )
    
    val separatorText = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
    
    val inputText = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
    
    val quickReplyText = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}