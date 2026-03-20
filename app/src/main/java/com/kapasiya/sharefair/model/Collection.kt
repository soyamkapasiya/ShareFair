package com.kapasiya.sharefair.model

import java.util.UUID

data class Collection(
    val id: String = UUID.randomUUID().toString(),
    val groupId: String = "",
    val name: String = "", // e.g. "Goa Trip"
    val description: String = "",
    val billIds: List<String> = emptyList(), // References to Bill IDs
    val totalAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
