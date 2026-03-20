package com.kapasiya.sharefair.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val totalBalance: Double = 0.0,
    val friends: List<String> = emptyList(),
    val groups: List<String> = emptyList(), // Group IDs the user is part of
    val isPremium: Boolean = false,
    val totalSpent: Double = 0.0,
    val groupLimit: Int = 5
)
