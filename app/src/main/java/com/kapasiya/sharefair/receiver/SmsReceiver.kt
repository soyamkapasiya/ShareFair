package com.kapasiya.sharefair.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.displayMessageBody
                val sender = message.displayOriginatingAddress
                
                Log.d("SmsReceiver", "Received SMS from $sender: $body")
                
                processSms(body, sender)
            }
        }
    }

    private fun processSms(body: String, sender: String) {
        // Simple regex to find amounts like "Rs. 500" or "INR 500" or "₹500"
        val amountPattern = Pattern.compile("(?:Rs|INR|₹)\\.?\\s*([\\d,]+\\.?\\d*)", Pattern.CASE_INSENSITIVE)
        val matcher = amountPattern.matcher(body)
        
        if (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "")
            val amount = amountStr?.toDoubleOrNull() ?: return
            
            // Detect vendor
            val vendor = when {
                body.contains("Swiggy", ignoreCase = true) -> "Swiggy"
                body.contains("Zomato", ignoreCase = true) -> "Zomato"
                body.contains("Blinkit", ignoreCase = true) -> "Blinkit"
                body.contains("Zepto", ignoreCase = true) -> "Zepto"
                body.contains("BigBasket", ignoreCase = true) -> "BigBasket"
                else -> "Transaction"
            }
            
            saveDraft(vendor, amount, body)
        }
    }

    private fun saveDraft(vendor: String, amount: Double, rawBody: String) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        
        val draft = hashMapOf(
            "userId" to userId,
            "title" to "Draft: $vendor",
            "amount" to amount,
            "vendor" to vendor,
            "rawBody" to rawBody,
            "timestamp" to System.currentTimeMillis(),
            "status" to "DRAFT"
        )
        
        db.collection("drafts")
            .add(draft)
            .addOnSuccessListener {
                Log.d("SmsReceiver", "Draft saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("SmsReceiver", "Error saving draft", e)
            }
    }
}
