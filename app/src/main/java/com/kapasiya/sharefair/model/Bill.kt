package com.kapasiya.sharefair.model

data class Bill(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val payerId: String = "",
    val splitType: String = "EQUAL",
    val participantsMap: Map<String, Double> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
