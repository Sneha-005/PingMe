package com.devsneha.pingme.presentation.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsneha.pingme.data.repository.ContactRepository
import com.devsneha.pingme.model.Contact
import com.devsneha.pingme.model.ConnectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow(ContactListState())
    val state: StateFlow<ContactListState> = _state.asStateFlow()
    
    private var allContacts: List<Contact> = emptyList()
    
    fun loadContacts() {
        Log.d("ContactListViewModel", "loadContacts() called")
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            Log.d("ContactListViewModel", "Loading contacts...")
            
            try {
                allContacts = contactRepository.getContacts()
                Log.d("ContactListViewModel", "Contacts loaded from repository: ${allContacts.size}")
                filterContacts()
                _state.update { it.copy(isLoading = false) }
                Log.d("ContactListViewModel", "Contacts loading completed. State contacts: ${_state.value.contacts.size}")
            } catch (e: Exception) {
                Log.e("ContactListViewModel", "Error loading contacts: ${e.message}", e)
                _state.update { 
                    it.copy(
                        error = e.message ?: "Failed to load contacts",
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun onSearchQueryChange(query: String) {
        Log.d("ContactListViewModel", "Search query changed to: '$query'")
        _state.update { it.copy(searchQuery = query) }
        filterContacts()
    }
    
    fun toggleWhatsAppFilter() {
        Log.d("ContactListViewModel", "WhatsApp filter toggled")
        _state.update { it.copy(showOnlyWhatsAppContacts = !it.showOnlyWhatsAppContacts) }
        filterContacts()
    }
    
    private fun filterContacts() {
        viewModelScope.launch {
            Log.d("ContactListViewModel", "filterContacts() called")
            Log.d("ContactListViewModel", "All contacts: ${allContacts.size}")
            Log.d("ContactListViewModel", "Show only WhatsApp: ${_state.value.showOnlyWhatsAppContacts}")
            Log.d("ContactListViewModel", "Search query: '${_state.value.searchQuery}'")
            
            var filteredContacts = allContacts
            
            // Apply WhatsApp filter
            if (_state.value.showOnlyWhatsAppContacts) {
                filteredContacts = filteredContacts.filter { it.isWhatsAppContact }
                Log.d("ContactListViewModel", "After WhatsApp filter: ${filteredContacts.size}")
            }
            
            // Apply search filter
            val searchQuery = _state.value.searchQuery
            if (searchQuery.isNotEmpty()) {
                filteredContacts = filteredContacts.filter { contact ->
                    contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.phoneNumber.contains(searchQuery, ignoreCase = true)
                }
                Log.d("ContactListViewModel", "After search filter: ${filteredContacts.size}")
            }
            
            _state.update { it.copy(contacts = filteredContacts) }
            Log.d("ContactListViewModel", "Final filtered contacts: ${filteredContacts.size}")
        }
    }
    
    fun clearError() {
        _state.update { it.copy(error = null) }
    }
    
    fun refreshContacts() {
        Log.d("ContactListViewModel", "refreshContacts() called")
        loadContacts()
    }
    
    fun sendChatRequest(contact: Contact) {
        viewModelScope.launch {
            // Here you would make an API call to send the request.
            // For now, we'll just update the local state to show the change in UI.
            contact.connectionStatus = ConnectionStatus.REQUEST_SENT
            
            // Create a new list with the updated contact
            val updatedContacts = _state.value.contacts.map {
                if (it.id == contact.id) contact else it
            }
            
            _state.update { it.copy(contacts = updatedContacts) }
        }
    }
}

data class ContactListState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val showOnlyWhatsAppContacts: Boolean = false
) 