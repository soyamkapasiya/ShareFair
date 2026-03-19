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
    suspend fun updateBill(groupId: String, bill: Bill)
    suspend fun settleUp(fromUserId: String, toUserId: String, amount: Double)
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
            
            // 1. Create the Bill with an ID
            val billWithId = bill.copy(id = billRef.id)
            transaction.set(billRef, billWithId)
            
            // 2. Update Group activity snippet
            transaction.update(groupRef, "recentActivity", FieldValue.arrayUnion("${bill.title}: ₹${bill.amount}"))

            // 3. Update Payer Balance
            // Payer is owed THE SUM OF ALL OTHER participants' shares.
            val payerShare = bill.participantsMap[bill.payerId] ?: 0.0
            val amountToBeRepaid = bill.amount - payerShare
            
            if (amountToBeRepaid > 0) {
                val payerRef = firestore.collection("users").document(bill.payerId)
                transaction.update(payerRef, "totalBalance", FieldValue.increment(amountToBeRepaid))
            }

            // 4. Update Other Participants' Balances
            // Everyone who was in the split (except the payer) owes their specific share.
            bill.participantsMap.forEach { (userId, share) ->
                if (userId != bill.payerId) {
                    val userRef = firestore.collection("users").document(userId)
                    transaction.update(userRef, "totalBalance", FieldValue.increment(-share))
                }
            }
            
            null
        }.await()
    }

    override suspend fun updateBill(groupId: String, bill: Bill) {
        getBillsCollection(groupId).document(bill.id).set(bill).await()
    }

    override suspend fun settleUp(fromUserId: String, toUserId: String, amount: Double) {
        firestore.runTransaction { transaction ->
            val fromUserRef = firestore.collection("users").document(fromUserId)
            val toUserRef = firestore.collection("users").document(toUserId)
            
            // FromUser (Payer) is paying back, so hide their debt (increase their totalBalance) 
            // wait, if I owe money my totalBalance is NEGATIVE.
            // If I pay ₹100, my totalBalance should increase by ₹100.
            transaction.update(fromUserRef, "totalBalance", FieldValue.increment(amount))
            
            // ToUser (Receiver) was getting money, so their totalBalance was POSITIVE.
            // If they get paid ₹100, they are owed ₹100 LESS now.
            transaction.update(toUserRef, "totalBalance", FieldValue.increment(-amount))
            
            null
        }.await()
    }
}
