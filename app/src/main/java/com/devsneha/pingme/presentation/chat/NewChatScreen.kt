//package com.devsneha.pingme.presentation.chat
//
//import androidx.compose.foundation.background
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
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.compose.AsyncImage
//import com.devsneha.pingme.model.User
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NewChatScreen(
//    onBackClick: () -> Unit,
//    onUserClick: (User) -> Unit,
//    viewModel: NewChatViewModel = hiltViewModel()
//) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("New Chat", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.Default.ArrowBack, "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Search bar
//            OutlinedTextField(
//                value = state.searchQuery,
//                onValueChange = viewModel::onSearchQueryChange,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                placeholder = { Text("Search users...") },
//                leadingIcon = {
//                    Icon(Icons.Default.Search, "Search")
//                },
//                singleLine = true
//            )
//
//            // User list
//            if (state.isLoading) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            } else if (state.users.isEmpty()) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "No users found",
//                        fontSize = 16.sp,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
//                }
//            } else {
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    contentPadding = PaddingValues(vertical = 8.dp)
//                ) {
//                    items(state.users) { user ->
//                        UserItem(
//                            user = user,
//                            onClick = { onUserClick(user) }
//                        )
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
//@Composable
//fun UserItem(
//    user: User,
//    onClick: () -> Unit
//) {
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
//            // Profile picture
//            AsyncImage(
//                model = user.profilePicture,
//                contentDescription = "Profile Picture",
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // User info
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = user.username,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = MaterialTheme.colorScheme.onSurface
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = user.phoneNumber.toString(),
//                    fontSize = 14.sp,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//
//                user.status?.let { status ->
//                    if (status.isNotEmpty()) {
//                        Spacer(modifier = Modifier.height(4.dp))
//                        Text(
//                            text = status,
//                            fontSize = 14.sp,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                            maxLines = 1
//                        )
//                    }
//                }
//            }
//
//            // Online indicator
//            if (user.isOnline) {
//                Box(
//                    modifier = Modifier
//                        .size(12.dp)
//                        .clip(CircleShape)
//                        .background(MaterialTheme.colorScheme.primary)
//                )
//            }
//        }
//    }
//}