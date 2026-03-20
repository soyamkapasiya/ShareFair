package com.kapasiya.sharefair.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kapasiya.sharefair.model.Notification
import kotlinx.coroutines.tasks.await

interface NotificationRepository {
    suspend fun sendNotification(userId: String, notification: Notification)
    suspend fun sendReminder(fromUserId: String, toUserId: String, amount: Double, message: String)
}

class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    override suspend fun sendNotification(userId: String, notification: Notification) {
        firestore.collection("users").document(userId)
            .collection("notifications").add(notification).await()
    }

    override suspend fun sendReminder(fromUserId: String, toUserId: String, amount: Double, message: String) {
        val notification = Notification(
            title = "Payment Reminder",
            message = "Your friend is asking for ₹$amount. Note: $message",
            time = "Just now"
        )
        sendNotification(toUserId, notification)
    }
}
