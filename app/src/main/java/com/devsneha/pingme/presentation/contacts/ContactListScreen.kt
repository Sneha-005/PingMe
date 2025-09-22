package com.devsneha.pingme.presentation.contacts

import android.Manifest
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.devsneha.pingme.model.Contact
import com.devsneha.pingme.model.ConnectionStatus
import com.google.accompanist.permissions.*
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star

@OptIn(ExperimentalMaterial3Api::class,ExperimentalPermissionsApi::class)
@Composable
fun ContactListScreen(
    onBackClick: () -> Unit,
    onContactClick: (Contact) -> Unit,
    viewModel: ContactListViewModel = hiltViewModel()
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Log.d("ContactListScreen", "Screen recomposed - Permission granted: ${permissionState.status.isGranted}")
    Log.d("ContactListScreen", "State - isLoading: ${state.isLoading}, contacts: ${state.contacts.size}, error: ${state.error}")

    LaunchedEffect(permissionState.status.isGranted) {
        Log.d("ContactListScreen", "LaunchedEffect triggered - Permission granted: ${permissionState.status.isGranted}")
        if (permissionState.status.isGranted) {
            Log.d("ContactListScreen", "Loading contacts...")
            viewModel.loadContacts()
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contacts", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshContacts() }) {
                        Icon(Icons.Default.Refresh, "Refresh Contacts")
                    }
                    IconButton(onClick = { viewModel.toggleWhatsAppFilter() }) {
                        Icon(
                            if (state.showOnlyWhatsAppContacts) Icons.Default.Person else Icons.Default.Person,
                            "WhatsApp Filter"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            if (permissionState.status.isGranted) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search contacts") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true
                )
            }

            if (!permissionState.status.isGranted) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Contact Permission Required",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "This app needs access to your contacts to show your contact list.",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { permissionState.launchPermissionRequest() }
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            } else {
                // Debug info
                if (state.isLoading || state.contacts.isEmpty()) {
                    Text(
                        text = "Debug: Loading=${state.isLoading}, Contacts=${state.contacts.size}, Error=${state.error}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                // Contact list
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.contacts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.searchQuery.isNotEmpty()){
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No contacts found for '${state.searchQuery}'",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "No contacts found",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(state.contacts) { contact ->
                            ContactItem(
                                contact = contact,
                                onContactClick = { onContactClick(contact) },
                                onSendRequestClick = { viewModel.sendChatRequest(contact) }
                            )
                        }
                    }
                }
            }
        }
        
        // Error dialog
        state.error?.let { error ->
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                title = { Text("Error") },
                text = { Text(error) },
                confirmButton = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onContactClick: () -> Unit,
    onSendRequestClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(
                enabled = contact.connectionStatus == ConnectionStatus.CONNECTED,
                onClick = onContactClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contact photo
            AsyncImage(
                model = contact.photoUri,
                contentDescription = "Contact Photo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contact info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // WhatsApp indicator
            if (contact.isWhatsAppContact) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "WhatsApp Contact",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Action button based on connection status
            when (contact.connectionStatus) {
                ConnectionStatus.NOT_CONNECTED -> {
                    Button(onClick = onSendRequestClick) {
                        Text("Request")
                    }
                }
                ConnectionStatus.REQUEST_SENT -> {
                    Text("Request Sent", color = MaterialTheme.colorScheme.primary)
                }
                ConnectionStatus.CONNECTED -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Connected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
} 