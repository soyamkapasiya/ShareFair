package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface GroupRepository {
    fun getGroupFlow(groupId: String): Flow<Group?>
    fun getGroupsForUserFlow(userId: String): Flow<List<Group>>
    suspend fun createGroup(group: Group)
    suspend fun updateGroup(group: Group)
}

class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore
) : GroupRepository {

    private val groupsCollection = firestore.collection("groups")

    override fun getGroupFlow(groupId: String): Flow<Group?> {
        return groupsCollection.document(groupId).snapshots().map { snapshot ->
            snapshot.toObject(Group::class.java)
        }
    }

    override fun getGroupsForUserFlow(userId: String): Flow<List<Group>> {
        return groupsCollection.whereArrayContains("members", userId).snapshots().map { snapshot ->
            snapshot.toObjects(Group::class.java)
        }
    }

    override suspend fun createGroup(group: Group) {
        val documentRef = if (group.id.isEmpty()) {
            groupsCollection.document()
        } else {
            groupsCollection.document(group.id)
        }
        val groupWithId = if (group.id.isEmpty()) group.copy(id = documentRef.id) else group
        documentRef.set(groupWithId).await()
    }

    override suspend fun updateGroup(group: Group) {
        groupsCollection.document(group.id).set(group).await()
    }
}
