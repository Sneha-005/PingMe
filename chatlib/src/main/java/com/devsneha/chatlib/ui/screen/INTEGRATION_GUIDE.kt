package com.devsneha.chatlib.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Quick Start Guide for Integrating Chat Library
 * 
 * Step 1: Add to your navigation
 * ================================
 * 
 * import androidx.navigation.compose.NavHost
 * import androidx.navigation.compose.composable
 * import com.devsneha.chatlib.ui.screen.ChatListScreen
 * import com.devsneha.chatlib.ui.screen.ChatDetailScreen
 * 
 * NavHost(navController, startDestination = "chat_list") {
 *     composable("chat_list") {
 *         ChatListScreen(
 *             userId = getCurrentUserId(),
 *             onChatSelected = { chat ->
 *                 navController.navigate("chat_detail/${chat.id}")
 *             },
 *             onCreateChatClick = { 
 *                 // Show create chat dialog
 *             }
 *         )
 *     }
 *     
 *     composable("chat_detail/{chatId}") { backStackEntry ->
 *         val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
 *         val chat = getChatById(chatId) // Get from your data source
 *         
 *         ChatDetailScreen(
 *             chat = chat,
 *             userId = getCurrentUserId(),
 *             userName = getCurrentUserName(),
 *             onBackClick = { navController.popBackStack() }
 *         )
 *     }
 * }
 * 
 * 
 * Step 2: Update ChatModule with your API base URL
 * ================================================
 * 
 * Open: chatlib/src/main/java/com/devsneha/chatlib/di/ChatModule.kt
 * 
 * Change:
 *     private const val CHAT_API_BASE_URL = "https://api.yourserver.com/"
 * 
 * To your actual backend API URL
 * 
 * 
 * Step 3: (Optional) Add Authentication
 * =====================================
 * 
 * In ChatModule.kt, modify the OkHttpClient builder:
 * 
 *     .addInterceptor { chain ->
 *         val token = getYourAuthToken()
 *         val request = chain.request().newBuilder()
 *             .addHeader("Authorization", "Bearer $token")
 *             .build()
 *         chain.proceed(request)
 *     }
 * 
 * 
 * Step 4: Make sure your MainActivity/App uses HiltAndroidApp
 * ===========================================================
 * 
 * @HiltAndroidApp
 * class PingMeApplication : Application()
 * 
 * 
 * Step 5: Add Hilt annotation to your Activity
 * ============================================
 * 
 * @AndroidEntryPoint
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         // Your code
 *     }
 * }
 * 
 * 
 * API Response Format Expected
 * ============================
 * 
 * For Chats:
 * {
 *     "success": true,
 *     "data": [
 *         {
 *             "id": "chat_1",
 *             "name": "John Doe",
 *             "type": "one_on_one",
 *             "members": [...],
 *             "lastMessage": "See you later!",
 *             "lastMessageTime": 1699999999999,
 *             "unreadCount": 2,
 *             "createdAt": 1699999999999,
 *             "isActive": true
 *         }
 *     ],
 *     "pagination": {
 *         "page": 1,
 *         "pageSize": 20,
 *         "totalItems": 50,
 *         "totalPages": 3
 *     }
 * }
 * 
 * For Messages:
 * {
 *     "success": true,
 *     "data": [
 *         {
 *             "id": "msg_1",
 *             "chatId": "chat_1",
 *             "senderId": "user_1",
 *             "senderName": "John",
 *             "text": "Hello!",
 *             "timestamp": 1699999999999,
 *             "isRead": true
 *         }
 *     ],
 *     "pagination": {...}
 * }
 * 
 * 
 * Troubleshooting
 * ===============
 * 
 * 1. "Cannot find symbol: ChatApiService"
 *    - Make sure ChatModule.kt is in the correct package
 *    - Run: Build > Clean Project > Rebuild Project
 * 
 * 2. Hilt compilation errors
 *    - Ensure @HiltAndroidApp is in your Application class
 *    - Add @AndroidEntryPoint to activities
 * 
 * 3. API returns 401 Unauthorized
 *    - Add authentication header in ChatModule.kt
 *    - Check your token is valid
 * 
 * 4. Messages not loading
 *    - Check base URL configuration
 *    - Verify API endpoint: GET /api/v1/chats/{chatId}/messages
 *    - Check network connectivity
 * 
 */

@Composable
fun IntegrationGuideScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("See the file header comments for integration guide")
    }
}
