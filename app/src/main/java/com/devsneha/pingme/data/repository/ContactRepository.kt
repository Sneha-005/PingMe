package com.devsneha.pingme.data.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.devsneha.pingme.model.Contact
import com.devsneha.pingme.model.ConnectionStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver = context.contentResolver
        
        Log.d("ContactRepository", "Starting to fetch contacts...")
        
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )
        
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1"
        
        try {
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                projection,
                selection,
                null,
                "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                Log.d("ContactRepository", "Cursor returned with ${cursor.count} contacts")
                
                while (cursor.moveToNext()) {
                    try {
                        val contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                        val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))
                        
                        Log.d("ContactRepository", "Processing contact: $name (ID: $contactId)")
                        
                        // Get phone numbers for this contact
                        val phoneNumbers = getPhoneNumbers(contentResolver, contactId)
                        
                        Log.d("ContactRepository", "Found ${phoneNumbers.size} phone numbers for $name")
                        
                        for (phoneNumber in phoneNumbers) {
                            val isWhatsAppContact = checkIfWhatsAppContact(phoneNumber)
                            contacts.add(
                                Contact(
                                    id = contactId,
                                    name = name ?: "Unknown",
                                    phoneNumber = phoneNumber,
                                    photoUri = photoUri,
                                    isWhatsAppContact = isWhatsAppContact,
                                    connectionStatus = ConnectionStatus.NOT_CONNECTED
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("ContactRepository", "Error processing contact: ${e.message}", e)
                    }
                }
            } ?: run {
                Log.e("ContactRepository", "Cursor is null - query failed")
            }
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error querying contacts: ${e.message}", e)
            throw e
        }
        
        Log.d("ContactRepository", "Total contacts loaded: ${contacts.size}")
        contacts
    }
    
    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: String): List<String> {
        val phoneNumbers = mutableListOf<String>()
        
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId)
        
        try {
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    if (!phoneNumber.isNullOrBlank()) {
                        phoneNumbers.add(phoneNumber)
                    }
                }
            } ?: run {
                Log.e("ContactRepository", "Phone number cursor is null for contact ID: $contactId")
            }
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error getting phone numbers for contact $contactId: ${e.message}", e)
        }
        
        return phoneNumbers
    }
    
    private fun checkIfWhatsAppContact(phoneNumber: String): Boolean {
        // This is a simplified check. In a real implementation, you might want to:
        // 1. Check if the number is registered on WhatsApp using WhatsApp Business API
        // 2. Use a third-party service to verify WhatsApp numbers
        // 3. Check local WhatsApp database (requires additional permissions)
        
        // For now, we'll return false as a placeholder
        // You can implement actual WhatsApp verification logic here
        return false
    }
    
    suspend fun searchContacts(query: String): List<Contact> = withContext(Dispatchers.IO) {
        val allContacts = getContacts()
        allContacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true) ||
            contact.phoneNumber.contains(query, ignoreCase = true)
        }
    }
    
    suspend fun getWhatsAppContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val allContacts = getContacts()
        allContacts.filter { it.isWhatsAppContact }
    }
    
    suspend fun getContactByPhoneNumber(phoneNumber: String): Contact? = withContext(Dispatchers.IO) {
        val allContacts = getContacts()
        allContacts.find { contact ->
            contact.phoneNumber.replace("\\s".toRegex(), "") == phoneNumber.replace("\\s".toRegex(), "")
        }
    }
} 