package com.kapasiya.sharefair.model

data class BillCollection(
    val id: String = "",
    val name: String = "",
    val userId: String = "",
    val groupIds: List<String> = emptyList(),
    val billIds: List<String> = emptyList(),
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
