package com.kapasiya.sharefair.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",
    val totalBalance: Double = 0.0,
    val friends: List<String> = emptyList(),
    val isPremium: Boolean = false,
    val groupLimit: Int = 5
)
