package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.BillCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface CollectionRepository {
    fun getCollectionsForUser(userId: String): Flow<List<BillCollection>>
    suspend fun createCollection(collection: BillCollection)
    suspend fun addBillToCollection(collectionId: String, billId: String)
}

class CollectionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CollectionRepository {

    private val collectionPath = firestore.collection("bill_collections")

    override fun getCollectionsForUser(userId: String): Flow<List<BillCollection>> {
        return collectionPath.whereEqualTo("userId", userId).snapshots().map { 
            it.toObjects(BillCollection::class.java) 
        }
    }

    override suspend fun createCollection(collection: BillCollection) {
        val doc = if (collection.id.isEmpty()) collectionPath.document() else collectionPath.document(collection.id)
        val finalCollection = if (collection.id.isEmpty()) collection.copy(id = doc.id) else collection
        doc.set(finalCollection).await()
    }

    override suspend fun addBillToCollection(collectionId: String, billId: String) {
        val docRef = collectionPath.document(collectionId)
        firestore.runTransaction { transaction ->
            val collection = transaction.get(docRef).toObject(BillCollection::class.java)
            collection?.let {
                if (!it.billIds.contains(billId)) {
                    transaction.update(docRef, "billIds", it.billIds + billId)
                }
            }
        }.await()
    }
}
