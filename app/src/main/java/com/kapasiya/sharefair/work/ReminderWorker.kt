package com.kapasiya.sharefair.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.kapasiya.sharefair.R
import kotlinx.coroutines.tasks.await

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db = FirebaseFirestore.getInstance()
        
        try {
            // Find all pending settlements that are older than 3 days
            // In a pro app, we'd check each group's balance and send smart reminders
            // For now, let's simulate checking a general 'reminders' queue or high-value debts
            
            val groups = db.collection("groups").get().await()
            for (group in groups.documents) {
                val balances = group.get("groupBalance") as? Map<String, Double> ?: continue
                
                balances.forEach { (userId, balance) ->
                    if (balance < -500.0) { // Large debt
                        sendSmartNotification(userId, balance)
                    }
                }
            }
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun sendSmartNotification(userId: String, amount: Double) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "smart_reminders"
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Smart Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Smart Debt Alert")
            .setContentText("Hey! You have an outstanding debt of ₹${kotlin.math.abs(amount)}. Let's settle up to keep things fair!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(userId.hashCode(), notification)
    }
}
