package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.BillDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface DraftRepository {
    fun getDraftsForUser(userId: String): Flow<List<BillDraft>>
    suspend fun deleteDraft(draftId: String)
    suspend fun updateDraftStatus(draftId: String, status: String)
}

class DraftRepositoryImpl(
    private val firestore: FirebaseFirestore
) : DraftRepository {

    private val draftsCollection = firestore.collection("drafts")

    override fun getDraftsForUser(userId: String): Flow<List<BillDraft>> {
        return draftsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", "DRAFT")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(BillDraft::class.java) }
    }

    override suspend fun deleteDraft(draftId: String) {
        draftsCollection.document(draftId).delete().await()
    }

    override suspend fun updateDraftStatus(draftId: String, status: String) {
        draftsCollection.document(draftId).update("status", status).await()
    }
}
