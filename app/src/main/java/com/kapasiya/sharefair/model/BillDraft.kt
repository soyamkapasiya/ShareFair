package com.kapasiya.sharefair.model

data class BillDraft(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val vendor: String = "Unknown",
    val rawBody: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "DRAFT", // DRAFT, CONVERTED
    val items: List<BillItem> = emptyList()
)
