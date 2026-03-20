package com.kapasiya.sharefair.model

data class BillItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String = "",
    val price: Double = 0.0,
    val consumedBy: List<String> = emptyList() // User IDs
)

data class Bill(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val payerId: String = "",
    val splitType: String = "EQUAL", // EQUAL, EXACT, ITEM_WISE
    val participantsMap: Map<String, Double> = emptyMap(),
    val items: List<BillItem> = emptyList(),
    val isRecurring: Boolean = false,
    val frequency: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
