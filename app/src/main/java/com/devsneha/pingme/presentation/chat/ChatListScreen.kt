//package com.devsneha.pingme.presentation.chat
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.compose.AsyncImage
//import com.devsneha.pingme.model.Conversation
//import com.devsneha.pingme.model.ConversationType
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.google.accompanist.permissions.rememberPermissionState
//import java.text.SimpleDateFormat
//import java.util.*
//import android.Manifest
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
//@Composable
//fun ChatListScreen(
//    onChatClick: (String) -> Unit,
//    onNewChatClick: () -> Unit,
//    onContactsClick: () -> Unit,
//    onProfileClick: () -> Unit,
//    viewModel: ChatListViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val contactsPermissionState = rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)
//
//    LaunchedEffect(contactsPermissionState.status) {
//        if (contactsPermissionState.status.isGranted) {
//            viewModel.loadConversations()
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Chats",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                },
//                actions = {
//                    IconButton(onClick = onContactsClick) {
//                        Icon(
//                            imageVector = Icons.Default.AccountCircle,
//                            contentDescription = "Contacts"
//                        )
//                    }
//                    IconButton(onClick = onProfileClick) {
//                        Icon(
//                            imageVector = Icons.Default.Person,
//                            contentDescription = "Profile"
//                        )
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onNewChatClick,
//                containerColor = MaterialTheme.colorScheme.primary
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Person,
//                    contentDescription = "New Chat",
//                    tint = Color.White
//                )
//            }
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            if (contactsPermissionState.status.isGranted) {
//                // Search bar
//                SearchBar(
//                    query = state.searchQuery,
//                    onQueryChange = viewModel::onSearchQueryChange,
//                    modifier = Modifier.padding(16.dp)
//                )
//
//                // Chat list
//                if (state.isLoading) {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                } else if (state.conversations.isEmpty()) {
//                    EmptyChatsView()
//                } else {
//                    LazyColumn(
//                        modifier = Modifier.fillMaxSize(),
//                        contentPadding = PaddingValues(vertical = 8.dp)
//                    ) {
//                        items(state.conversations) { conversation ->
//                            ConversationItem(
//                                conversation = conversation,
//                                onClick = {
//                                    if (conversation.type == ConversationType.INDIVIDUAL) {
//                                        // For individual chats, we need to find the other user
//                                        val otherUser = conversation.participants.firstOrNull { it.id != 1L }
//                                        otherUser?.let { onChatClick("user/${it.id}") }
//                                    } else {
//                                        onChatClick("conversation/${conversation.id}")
//                                    }
//                                }
//                            )
//                        }
//
//                        // Load more button
//                        if (state.hasMoreConversations) {
//                            item {
//                                TextButton(
//                                    onClick = viewModel::loadMoreConversations,
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    Text("Load More")
//                                }
//                            }
//                        }
//                    }
//                }
//            } else {
//                Column(
//                    modifier = Modifier.fillMaxSize().padding(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text("Contact Permission Required", style = MaterialTheme.typography.headlineSmall)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Text("This app needs access to your contacts to find friends.", textAlign = TextAlign.Center)
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Button(onClick = { contactsPermissionState.launchPermissionRequest() }) {
//                        Text("Grant Permission")
//                    }
//                }
//            }
//        }
//
//        // Error dialog
//        state.error?.let { error ->
//            AlertDialog(
//                onDismissRequest = viewModel::clearError,
//                title = { Text("Error") },
//                text = { Text(error) },
//                confirmButton = {
//                    TextButton(onClick = viewModel::clearError) {
//                        Text("OK")
//                    }
//                }
//            )
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBar(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    OutlinedTextField(
//        value = query,
//        onValueChange = onQueryChange,
//        modifier = modifier.fillMaxWidth(),
//        placeholder = { Text("Search conversations...") },
//        leadingIcon = {
//            Icon(
//                imageVector = Icons.Default.Search,
//                contentDescription = "Search"
//            )
//        },
//        trailingIcon = {
//            if (query.isNotEmpty()) {
//                IconButton(onClick = { onQueryChange("") }) {
//                    Icon(
//                        imageVector = Icons.Default.Clear,
//                        contentDescription = "Clear"
//                    )
//                }
//            }
//        },
//        singleLine = true,
//        colors = OutlinedTextFieldDefaults.colors(
//            focusedBorderColor = MaterialTheme.colorScheme.primary,
//            unfocusedBorderColor = MaterialTheme.colorScheme.outline
//        )
//    )
//}
//
//@Composable
//fun ConversationItem(
//    conversation: Conversation,
//    onClick: () -> Unit
//) {
//    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp)
//            .clickable(onClick = onClick),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surface
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Profile picture or group icon
//            Box(
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape),
//                contentAlignment = Alignment.Center
//            ) {
//                if (conversation.type == ConversationType.GROUP) {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "Group",
//                        tint = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.size(30.dp)
//                    )
//                } else {
//                    // For individual chats, show the other user's profile picture
//                    val otherUser = conversation.participants.firstOrNull { it.id != 1L }
//                    AsyncImage(
//                        model = otherUser?.profilePicture,
//                        contentDescription = "Profile Picture",
//                        contentScale = ContentScale.Crop,
//                        error = @Composable {
//                            Icon(
//                                imageVector = Icons.Default.Person,
//                                contentDescription = null,
//                                tint = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        } as Painter?
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Conversation info
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = conversation.name,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//
//                    Text(
//                        text = timeFormat.format(conversation.lastMessageAt),
//                        fontSize = 12.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "${conversation.participants.size} participants",
//                        fontSize = 14.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    // Show conversation type indicator
//                    if (conversation.type == ConversationType.GROUP) {
//                        Badge(
//                            containerColor = MaterialTheme.colorScheme.secondary
//                        ) {
//                            Text(
//                                text = "GROUP",
//                                color = MaterialTheme.colorScheme.onSecondary,
//                                fontSize = 10.sp
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun EmptyChatsView() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Default.Person,
//                contentDescription = null,
//                modifier = Modifier.size(64.dp),
//                tint = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "No conversations yet",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = "Start a new conversation to begin chatting",
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}