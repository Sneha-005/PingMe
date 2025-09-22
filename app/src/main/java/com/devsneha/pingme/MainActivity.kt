package com.devsneha.pingme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.devsneha.pingme.presentation.chat.ChatListScreen
import com.devsneha.pingme.presentation.chat.NewChatScreen
import com.devsneha.pingme.presentation.chat.SimpleChatScreen
import com.devsneha.pingme.presentation.contacts.ContactListScreen
import com.devsneha.pingme.ui.theme.PingMeTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.devsneha.pingme.presentation.auth.RegisterScreen
import com.devsneha.pingme.model.Contact
import com.devsneha.pingme.model.User

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PingMeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth_graph") {
                    navigation(startDestination = "register", route = "auth_graph") {
                        composable("register") {
                            RegisterScreen(
                                onNavigateToChatList = {
                                    navController.navigate("chat_list") {
                                        popUpTo("auth_graph") {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                    }

                    composable("chat_list") {
                        ChatListScreen(
                            onChatClick = { route ->
                                navController.navigate(route)
                            },
                            onNewChatClick = {
                                navController.navigate("new_chat")
                            },
                            onContactsClick = {
                                navController.navigate("contacts")
                            },
                            onProfileClick = {
                                // TODO: Navigate to profile screen
                            }
                        )
                    }
                    composable("contacts") {
                        ContactListScreen(
                            onBackClick = { navController.popBackStack() },
                            onContactClick = { contact ->
                                // Navigate to chat with the selected contact
                                navController.navigate("user/${contact.id}")
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(
                        route = "conversation/{conversationId}",
                        arguments = listOf(
                            navArgument("conversationId") { type = NavType.StringType }
                        )
                    ) {
                        SimpleChatScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "user/{userId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.StringType }
                        )
                    ) {
                        SimpleChatScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable("new_chat") {
                        NewChatScreen(
                            onBackClick = { navController.popBackStack() },
                            onUserClick = { user: Contact ->
                                // Navigate to individual chat with the selected user
                                navController.navigate("user/${user.id}")
                                navController.popBackStack()
                            } as (User) -> Unit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PingMeTheme {
        Greeting("Android")
    }
}