package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface UserRepository {
    fun getUserFlow(userId: String): Flow<User?>
    suspend fun saveUserProfile(user: User)
    suspend fun getUserProfile(userId: String): User?
    suspend fun findUserByEmail(email: String): User?
    suspend fun addFriend(userId: String, friendId: String)
    fun getFriends(friendIds: List<String>): Flow<List<User>>
}

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val usersCollection = firestore.collection("users")

    override fun getUserFlow(userId: String): Flow<User?> {
        return usersCollection.document(userId).snapshots().map { snapshot ->
            snapshot.toObject(User::class.java)
        }
    }

    override suspend fun saveUserProfile(user: User) {
        usersCollection.document(user.id).set(user).await()
    }

    override suspend fun getUserProfile(userId: String): User? {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    override suspend fun findUserByEmail(email: String): User? {
        val snapshot = usersCollection.whereEqualTo("email", email).get().await()
        return snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    override suspend fun addFriend(userId: String, friendId: String) {
        val userRef = usersCollection.document(userId)
        val friendRef = usersCollection.document(friendId)
        
        firestore.runTransaction { transaction ->
            val user = transaction.get(userRef).toObject(User::class.java)
            val friend = transaction.get(friendRef).toObject(User::class.java)
            
            if (user != null && friend != null) {
                if (!user.friends.contains(friendId)) {
                    transaction.update(userRef, "friends", (user.friends ?: emptyList()) + friendId)
                }
                if (!friend.friends.contains(userId)) {
                    transaction.update(friendRef, "friends", (friend.friends ?: emptyList()) + userId)
                }
            }
        }.await()
    }

    override fun getFriends(friendIds: List<String>): Flow<List<User>> {
        if (friendIds.isEmpty()) return flowOf(emptyList())
        return usersCollection.whereIn("id", friendIds).snapshots().map { snapshot ->
            snapshot.toObjects(User::class.java)
        }
    }
}
