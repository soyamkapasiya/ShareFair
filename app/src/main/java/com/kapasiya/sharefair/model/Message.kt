package com.kapasiya.sharefair.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val expenseId: String? = null // For discussing specific expenses
)
