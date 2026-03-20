package com.kapasiya.sharefair.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kapasiya.sharefair.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface ChatRepository {
    fun getMessagesForGroup(groupId: String): Flow<List<Message>>
    suspend fun sendMessage(groupId: String, message: Message)
}

class ChatRepositoryImpl(
    private val database: FirebaseDatabase
) : ChatRepository {

    private fun getChatRef(groupId: String) = 
        database.getReference("chats").child(groupId)

    override fun getMessagesForGroup(groupId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (child in snapshot.children) {
                    val msg = child.getValue(Message::class.java)
                    if (msg != null) messages.add(msg)
                }
                trySend(messages.sortedBy { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        val ref = getChatRef(groupId)
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun sendMessage(groupId: String, message: Message) {
        val ref = getChatRef(groupId).push()
        val key = ref.key ?: return
        ref.setValue(message.copy(id = key)).await()
    }
}
