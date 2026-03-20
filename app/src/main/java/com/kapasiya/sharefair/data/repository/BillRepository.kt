package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.kapasiya.sharefair.model.Bill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

interface BillRepository {
    fun getBillsForGroupFlow(groupId: String): Flow<List<Bill>>
    suspend fun addBill(groupId: String, bill: Bill)
    suspend fun deleteBill(groupId: String, bill: Bill)
    suspend fun settleUp(groupId: String, fromUserId: String, toUserId: String, amount: Double)
}

class BillRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BillRepository {

    private fun getBillsCollection(groupId: String) = 
        firestore.collection("groups").document(groupId).collection("bills")

    override fun getBillsForGroupFlow(groupId: String): Flow<List<Bill>> {
        return getBillsCollection(groupId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(Bill::class.java) }
    }

    override suspend fun addBill(groupId: String, bill: Bill) {
        firestore.runTransaction { transaction ->
            val billsCollection = getBillsCollection(groupId)
            val billRef = billsCollection.document()
            val groupRef = firestore.collection("groups").document(groupId)
            
            val billWithId = bill.copy(id = billRef.id)
            transaction.set(billRef, billWithId)
            
            transaction.update(groupRef, "recentActivity", FieldValue.arrayUnion("${bill.title}: ₹${bill.amount}"))
            
            updateBalances(transaction, groupId, bill, 1.0)
            
            null
        }.await()
    }

    override suspend fun deleteBill(groupId: String, bill: Bill) {
        firestore.runTransaction { transaction ->
            val billRef = getBillsCollection(groupId).document(bill.id)
            val groupRef = firestore.collection("groups").document(groupId)
            
            transaction.delete(billRef)
            transaction.update(groupRef, "recentActivity", FieldValue.arrayUnion("Deleted: ${bill.title}"))
            
            // Reverse the balances (multiplier -1.0)
            updateBalances(transaction, groupId, bill, -1.0)
            
            null
        }.await()
    }

    private fun updateBalances(transaction: com.google.firebase.firestore.Transaction, groupId: String, bill: Bill, multiplier: Double) {
        val groupRef = firestore.collection("groups").document(groupId)
        
        // Payer share
        val payerShare = bill.participantsMap[bill.payerId] ?: 0.0
        val amountToBeRepaid = (bill.amount - payerShare) * multiplier
        
        // Update Payer's balances
        transaction.update(groupRef, "groupBalance.${bill.payerId}", FieldValue.increment(amountToBeRepaid))
        val payerRef = firestore.collection("users").document(bill.payerId)
        transaction.update(payerRef, "totalBalance", FieldValue.increment(amountToBeRepaid))

        // Update Other Participants
        bill.participantsMap.forEach { (userId, share) ->
            if (userId != bill.payerId) {
                val debt = -share * multiplier
                transaction.update(groupRef, "groupBalance.$userId", FieldValue.increment(debt))
                val userRef = firestore.collection("users").document(userId)
                transaction.update(userRef, "totalBalance", FieldValue.increment(debt))
            }
        }
    }

    override suspend fun settleUp(groupId: String, fromUserId: String, toUserId: String, amount: Double) {
        firestore.runTransaction { transaction ->
            val fromUserRef = firestore.collection("users").document(fromUserId)
            val toUserRef = firestore.collection("users").document(toUserId)
            val groupRef = firestore.collection("groups").document(groupId)
            
            transaction.update(fromUserRef, "totalBalance", FieldValue.increment(amount))
            transaction.update(toUserRef, "totalBalance", FieldValue.increment(-amount))
            
            transaction.update(groupRef, "groupBalance.$fromUserId", FieldValue.increment(amount))
            transaction.update(groupRef, "groupBalance.$toUserId", FieldValue.increment(-amount))
            
            val settlementBill = Bill(
                title = "Settle Up",
                amount = amount,
                payerId = fromUserId,
                participantsMap = mapOf(toUserId to amount),
                splitType = "SETTLEMENT",
                timestamp = System.currentTimeMillis()
            )
            val settlementRef = getBillsCollection(groupId).document()
            transaction.set(settlementRef, settlementBill.copy(id = settlementRef.id))
            
            null
        }.await()
    }
}
