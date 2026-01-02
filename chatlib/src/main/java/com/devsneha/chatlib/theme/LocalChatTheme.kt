package com.devsneha.chatlib.theme

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal for providing the current chat theme throughout the composition tree
 */
val LocalChatTheme = compositionLocalOf<ChatTheme> {
    ChatThemes.DefaultBlue // Default theme
}

