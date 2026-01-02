package com.devsneha.chatlib.theme

import androidx.compose.ui.graphics.Color

/**
 * Complete theme configuration for the chat UI
 */
data class ChatTheme(
    val name: String,
    // Message bubble colors
    val sentMessageBackground: Color,
    val receivedMessageBackground: Color,
    val sentMessageText: Color,
    val receivedMessageText: Color,

    // Send button gradient
    val sendButtonGradientStart: Color,
    val sendButtonGradientEnd: Color,

    // Status colors
    val messageStatusSent: Color,
    val messageStatusDelivered: Color,
    val messageStatusRead: Color,

    // Input colors
    val inputBackground: Color,
    val inputText: Color,
    val inputPlaceholder: Color,

    // Background colors
    val chatBackground: Color,
    val chatBackgroundGradientStart: Color, // Upper color - customizable
    val chatBackgroundGradientEnd: Color,

    // Avatar colors
    val avatarBackground: Color,
    val avatarText: Color,

    // Separator colors
    val separatorBackground: Color,
    val separatorText: Color,

    // Typing indicator
    val typingIndicator: Color,

    // Quick replies
    val quickReplyBackground: Color,
    val quickReplyBorder: Color,
    val quickReplyText: Color
)

object ChatThemes {

    // New palette themes (sampled from your palette image)
    val AmethystTeal = ChatTheme(
        name = "Amethyst Teal Contrast",
        sentMessageBackground = Color(0xFF7B61D6),
        receivedMessageBackground = Color.White,
        sentMessageText = Color.White,
        receivedMessageText = Color.Black,
        sendButtonGradientStart = Color(0xFF9B7DD6), // Medium purple from gradient
        sendButtonGradientEnd = Color(0xFF4A9A95), // Medium teal from gradient
        messageStatusSent = Color(0xFF9AA4B2),
        messageStatusDelivered = Color(0xFF9AA4B2),
        messageStatusRead = Color(0xFFF9C846), // contrast accent
        inputBackground = Color.White,
        inputText = Color.Black,
        inputPlaceholder = Color(0xFF8E8E93),
        chatBackground = Color(0xFFF5F7FB),
        chatBackgroundGradientStart = Color(0xFFB799E6),
        chatBackgroundGradientEnd = Color(0xFF0E918C),
        avatarBackground = Color(0xFFF9C846),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFF9AA4B2),
        typingIndicator = Color(0xFF9AA4B2),
        quickReplyBackground = Color.White,
        quickReplyBorder = Color(0xFFF9C846),
        quickReplyText = Color(0xFFF9C846)
    )


    val TealLime = ChatTheme(
        name = "Teal Lime",
        sentMessageBackground = Color(0xFF2FA98A),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFF2FA98A), // Dark teal-green from gradient
        sendButtonGradientEnd = Color(0xFF7ECF2A), // Vibrant lime green from gradient
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFF0D9488),
        inputBackground = Color(0xFFFFFFFF),
        inputText = Color(0xFF122029),
        inputPlaceholder = Color(0xFF9DAFB3),
        chatBackground = Color(0xFFF8FFFC),
        chatBackgroundGradientStart = Color(0xFF4FD1C5),
        chatBackgroundGradientEnd = Color(0xFF9CCF2A),
        avatarBackground = Color(0xFF0D9488),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFF9DAFB3),
        typingIndicator = Color(0xFF9DAFB3),
        quickReplyBackground = Color(0xFFFFFFFF),
        quickReplyBorder = Color(0xFF0D9488),
        quickReplyText = Color(0xFF0D9488)
    )

    val MintPastel = ChatTheme(
        name = "Mint Pastel",
        sentMessageBackground = Color(0xFF7EE1C9),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFF9EE1C9), // Mint green from gradient
        sendButtonGradientEnd = Color(0xFFFFD4A0), // Light yellow to soft orange from gradient
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFF7EE1C9),
        inputBackground = Color(0xFFFFFFFF),
        inputText = Color(0xFF112026),
        inputPlaceholder = Color(0xFF9DAFB3),
        chatBackground = Color(0xFFFFFFFF),
        chatBackgroundGradientStart = Color(0xFFBFF4E6),
        chatBackgroundGradientEnd = Color(0xFFF9C7D1),
        avatarBackground = Color(0xFF7EE1C9),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFF9DAFB3),
        typingIndicator = Color(0xFF9DAFB3),
        quickReplyBackground = Color(0xFFFFFFFF),
        quickReplyBorder = Color(0xFF7EE1C9),
        quickReplyText = Color(0xFF7EE1C9)
    )

    val NavySky = ChatTheme(
        name = "Navy Sky",
        sentMessageBackground = Color(0xFF0D3A76),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFF1E4A8F), // Vibrant medium blue from gradient
        sendButtonGradientEnd = Color(0xFF7DD0F0), // Light sky blue from gradient
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFF0D3A76),
        inputBackground = Color(0xFFF7FBFF),
        inputText = Color(0xFF0F2132),
        inputPlaceholder = Color(0xFF9FB7C9),
        chatBackground = Color(0xFFF8FBFF),
        chatBackgroundGradientStart = Color(0xFF052B59),
        chatBackgroundGradientEnd = Color(0xFF9DDCF6),
        avatarBackground = Color(0xFF1E40AF),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFF9FB7C9),
        typingIndicator = Color(0xFF9FB7C9),
        quickReplyBackground = Color(0xFFFFFFFF),
        quickReplyBorder = Color(0xFF1E40AF),
        quickReplyText = Color(0xFF1E40AF)
    )

    val SunsetOrchard = ChatTheme(
        name = "Sunset Orchard",
        sentMessageBackground = Color(0xFFD9572F),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFFFF8A55), // Bright orange from gradient
        sendButtonGradientEnd = Color(0xFF7ED07C), // Light aqua-green from gradient
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFFD9572F),
        inputBackground = Color(0xFFFFFBF8),
        inputText = Color(0xFF2B2B2B),
        inputPlaceholder = Color(0xFFBFAEA2),
        chatBackground = Color(0xFFFFFCFB),
        chatBackgroundGradientStart = Color(0xFFFF7A45),
        chatBackgroundGradientEnd = Color(0xFF84D07C),
        avatarBackground = Color(0xFFFF7A45),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFFBFAEA2),
        typingIndicator = Color(0xFFBFAEA2),
        quickReplyBackground = Color(0xFFFFFFFF),
        quickReplyBorder = Color(0xFFFF7A45),
        quickReplyText = Color(0xFFFF7A45)
    )

    val PeachSand = ChatTheme(
        name = "Peach Sand",
        sentMessageBackground = Color(0xFFF39D85),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFFFFB8A6), // Light rose pink from gradient
        sendButtonGradientEnd = Color(0xFFFFD4A0), // Light peach from gradient
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFFF39D85),
        inputBackground = Color(0xFFFFFEFD),
        inputText = Color(0xFF2A2A2A),
        inputPlaceholder = Color(0xFFBBAFA7),
        chatBackground = Color(0xFFFFFEFD),
        chatBackgroundGradientStart = Color(0xFFF7B8A6),
        chatBackgroundGradientEnd = Color(0xFFE9DAC0),
        avatarBackground = Color(0xFFF39D85),
        avatarText = Color.White,
        separatorBackground = Color(0xFFECECEC),
        separatorText = Color(0xFFBBAFA7),
        typingIndicator = Color(0xFFBBAFA7),
        quickReplyBackground = Color(0xFFFFFFFF),
        quickReplyBorder = Color(0xFFF39D85),
        quickReplyText = Color(0xFFF39D85)
    )

    // Keep some original/sample themes you had (optional)
    val DefaultBlue = ChatTheme(
        name = "Default Blue",
        sentMessageBackground = Color(0xFF1E40AF),
        receivedMessageBackground = Color(0xFFFFFFFF),
        sentMessageText = Color.White,
        receivedMessageText = Color(0xFF000000),
        sendButtonGradientStart = Color(0xFF60A5FA),
        sendButtonGradientEnd = Color(0xFF1E40AF),
        messageStatusSent = Color(0xFF8E8E93),
        messageStatusDelivered = Color(0xFF8E8E93),
        messageStatusRead = Color(0xFF007AFF),
        inputBackground = Color.White,
        inputText = Color.Black,
        inputPlaceholder = Color(0xFF8E8E93),
        chatBackground = Color(0xFFF2F2F7),
        chatBackgroundGradientStart = Color(0xFFE6F0FA),
        chatBackgroundGradientEnd = Color(0xFFFFFFFF),
        avatarBackground = Color(0xFF007AFF),
        avatarText = Color.White,
        separatorBackground = Color(0xFFE5E5EA),
        separatorText = Color(0xFF8E8E93),
        typingIndicator = Color(0xFF8E8E93),
        quickReplyBackground = Color.White,
        quickReplyBorder = Color(0xFF007AFF),
        quickReplyText = Color(0xFF007AFF)
    )

    // Aggregate
    fun getAllThemes(): List<ChatTheme> = listOf(
        AmethystTeal,
        TealLime,
        MintPastel,
        NavySky,
        SunsetOrchard,
        PeachSand,
        // optionally include original themes
        DefaultBlue
    )

    fun getThemeByName(name: String): ChatTheme? {
        return getAllThemes().find { it.name == name }
    }
}
