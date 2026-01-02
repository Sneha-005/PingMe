package com.devsneha.chatlib.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Legacy ChatColors object for backward compatibility
 * Use LocalChatTheme.current for theme-aware colors
 */
object ChatColors {
    // Message bubble colors
    val sentMessageBackground = Color(0xFF1E40AF) // Dark navy blue for sent messages
    val receivedMessageBackground = Color(0xFFFFFFFF) // White for received messages
    val sentMessageText = Color.White
    val receivedMessageText = Color(0xFF000000) // Dark grey/black for received messages
    
    // Status colors
    val messageStatusSent = Color(0xFF8E8E93)
    val messageStatusDelivered = Color(0xFF8E8E93)
    val messageStatusRead = Color(0xFF007AFF)
    
    // Input colors
    val inputBackground = Color.White
    val inputText = Color.Black
    val inputPlaceholder = Color(0xFF8E8E93)
    
    // Background colors
    val chatBackground = Color(0xFFF2F2F7)
    val chatBackgroundGradientStart = Color(0xFFE6F0FA) // Light blue - top of gradient
    val chatBackgroundGradientEnd = Color(0xFFFFFFFF) // White - bottom of gradient
    
    // Avatar colors
    val avatarBackground = Color(0xFF007AFF)
    val avatarText = Color.White
    
    // Separator colors
    val separatorBackground = Color(0xFFE5E5EA)
    val separatorText = Color(0xFF8E8E93)
    
    // Typing indicator
    val typingIndicator = Color(0xFF8E8E93)
    
    // Quick replies
    val quickReplyBackground = Color.White
    val quickReplyBorder = Color(0xFF007AFF)
    val quickReplyText = Color(0xFF007AFF)
}

/**
 * Extension functions to get colors from the current theme
 */
@Composable
fun chatThemeColors(): ChatTheme = LocalChatTheme.current