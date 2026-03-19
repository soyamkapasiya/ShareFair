package com.kapasiya.sharefair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kapasiya.sharefair.model.Notification
import com.kapasiya.sharefair.ui.theme.ShareFairTheme
import com.kapasiya.sharefair.ui.screens.NotificationScreen

class NotificationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val notifications = listOf(
            Notification("Dinner Split", "Vineet added a new bill for Dinner", "2m ago"),
            Notification("Payment Received", "Sahil paid you ₹500", "1h ago"),
            Notification("Group Created", "You were added to 'Goa Trip'", "3h ago"),
            Notification("Reminder", "Rent payment is due tomorrow", "1d ago"),
            Notification("Bill Shared", "A new bill has been shared with you", "2d ago"),
            Notification("Split Updated", "The expense split for 'Movie' has been updated", "3d ago")
        )

        setContent {
            ShareFairTheme {
                NotificationScreen(notifications) { finish() }
            }
        }
    }
}
