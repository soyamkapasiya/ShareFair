package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.RecurringBill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface RecurringBillRepository {
    fun getRecurringBillsForGroup(groupId: String): Flow<List<RecurringBill>>
    suspend fun addRecurringBill(recurringBill: RecurringBill)
    suspend fun deleteRecurringBill(id: String)
}

class RecurringBillRepositoryImpl(
    private val firestore: FirebaseFirestore
) : RecurringBillRepository {

    private val collection = firestore.collection("recurring_bills")

    override fun getRecurringBillsForGroup(groupId: String): Flow<List<RecurringBill>> {
        return collection.whereEqualTo("groupId", groupId).snapshots().map { 
            it.toObjects(RecurringBill::class.java) 
        }
    }

    override suspend fun addRecurringBill(recurringBill: RecurringBill) {
        val doc = if (recurringBill.id.isEmpty()) collection.document() else collection.document(recurringBill.id)
        val bill = if (recurringBill.id.isEmpty()) recurringBill.copy(id = doc.id) else recurringBill
        doc.set(bill).await()
    }

    override suspend fun deleteRecurringBill(id: String) {
        collection.document(id).delete().await()
    }
}
