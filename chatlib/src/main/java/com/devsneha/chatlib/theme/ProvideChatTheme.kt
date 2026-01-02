package com.devsneha.chatlib.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Provides a chat theme to all child composables
 * 
 * Usage:
 * ```
 * ProvideChatTheme(theme = ChatThemes.PurpleTeal) {
 *     ChatScreen(...)
 * }
 * ```
 */
@Composable
fun ProvideChatTheme(
    theme: ChatTheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalChatTheme provides theme) {
        content()
    }
}

