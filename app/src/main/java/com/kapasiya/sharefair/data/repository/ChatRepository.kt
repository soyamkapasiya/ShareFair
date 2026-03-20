package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface ChatRepository {
    fun getMessagesForGroup(groupId: String): Flow<List<Message>>
    suspend fun sendMessage(groupId: String, message: Message)
}

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    private fun getChatCollection(groupId: String) = 
        firestore.collection("groups").document(groupId).collection("chats")

    override fun getMessagesForGroup(groupId: String): Flow<List<Message>> {
        return getChatCollection(groupId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { it.toObjects(Message::class.java) }
    }

    override suspend fun sendMessage(groupId: String, message: Message) {
        val doc = getChatCollection(groupId).document()
        getChatCollection(groupId).document(doc.id).set(message.copy(id = doc.id)).await()
    }
}
