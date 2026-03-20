package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel(private val groupId: String) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    private val currentUserName = auth.currentUser?.displayName ?: "User"

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        observeMessages()
    }

    private fun observeMessages() {
        if (groupId.isEmpty()) return
        
        viewModelScope.launch {
            firestore.collection("groups").document(groupId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .snapshots()
                .map { it.toObjects(Message::class.java) }
                .collect {
                    _messages.value = it
                }
        }
    }

    fun sendMessage(text: String, expenseId: String? = null) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            val message = Message(
                senderId = currentUserId,
                senderName = currentUserName,
                text = text,
                timestamp = System.currentTimeMillis(),
                expenseId = expenseId
            )
            firestore.collection("groups").document(groupId).collection("messages")
                .add(message)
                .await()
        }
    }
}
