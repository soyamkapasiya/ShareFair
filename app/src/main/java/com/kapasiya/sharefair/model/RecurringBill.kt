package com.kapasiya.sharefair.model

data class RecurringBill(
    val id: String = "",
    val groupId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val payerId: String = "",
    val splitType: String = "EQUAL",
    val participantsMap: Map<String, Double> = emptyMap(),
    val frequency: String = "MONTHLY", // WEEKLY, MONTHLY
    val lastGeneratedTimestamp: Long = System.currentTimeMillis()
)
