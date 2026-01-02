package com.devsneha.chatlib.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devsneha.chatlib.model.Chat
import com.devsneha.chatlib.model.ChatMember
import com.devsneha.chatlib.model.ChatType

/**
 * Example Chat Navigation Setup
 * Shows how to integrate ChatListScreen and ChatDetailScreen with Navigation
 */

@Composable
fun ChatNavigationHost(
    userId: String,
    userName: String
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "chat_list"
    ) {
        composable("chat_list") {
            ChatListScreen(
                userId = userId,
                onChatSelected = { chat ->
                    navController.navigate("chat_detail/${chat.id}")
                },
                onCreateChatClick = {
                    navController.navigate("create_chat")
                }
            )
        }
        
        composable("chat_detail/{chatId}") { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
            
            // In real app, fetch chat from your data source
            // For now, creating a sample chat
            val sampleChat = Chat(
                id = chatId,
                name = "Sample Chat",
                type = ChatType.ONE_ON_ONE,
                members = listOf(
                    ChatMember(
                        id = userId,
                        name = userName,
                        joinedAt = System.currentTimeMillis()
                    ),
                    ChatMember(
                        id = "user_2",
                        name = "Other User",
                        joinedAt = System.currentTimeMillis()
                    )
                ),
                createdAt = System.currentTimeMillis()
            )
            
            ChatDetailScreen(
                chat = sampleChat,
                userId = userId,
                userName = userName,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable("create_chat") {
            CreateChatScreen(
                onChatCreated = { chat ->
                    navController.navigate("chat_detail/${chat.id}") {
                        popUpTo("chat_list")
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun CreateChatScreen(
    onChatCreated: (Chat) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Create New Chat",
            style = MaterialTheme.typography.headlineSmall
        )
        
        // TODO: Implement create chat UI
        // This should include:
        // - Chat type selector (One-on-one, Group, Channel)
        // - Member selection
        // - Chat name input
        // - Description input
        // - Create button
    }
}

/**
 * Tabbed Chat Interface Example
 * Shows how to display chat list with multiple tabs
 */
@Composable
fun TabbedChatScreen(
    userId: String,
    userName: String
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val tabs = listOf("Chats", "Groups", "Channels")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        
        when (selectedTabIndex) {
            0 -> {
                // All chats
                ChatListScreen(
                    userId = userId,
                    onChatSelected = { chat -> /* Navigate to chat */ },
                    onCreateChatClick = { /* Show create dialog */ }
                )
            }
            1 -> {
                // Groups only
                // TODO: Filter and show only groups
                Text("Groups Coming Soon")
            }
            2 -> {
                // Channels only
                // TODO: Filter and show only channels
                Text("Channels Coming Soon")
            }
        }
    }
}

/**
 * Full-screen Chat Example
 * Simple implementation for full screen chat view
 */
@Composable
fun FullScreenChatExample(
    chatId: String,
    userId: String,
    userName: String
) {
    val sampleChat = Chat(
        id = chatId,
        name = "John Doe",
        type = ChatType.ONE_ON_ONE,
        members = emptyList(),
        createdAt = System.currentTimeMillis()
    )
    
    ChatDetailScreen(
        chat = sampleChat,
        userId = userId,
        userName = userName,
        onBackClick = { /* Handle back */ }
    )
}
