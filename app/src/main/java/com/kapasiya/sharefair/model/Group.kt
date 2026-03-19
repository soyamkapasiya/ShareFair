package com.kapasiya.sharefair.model

data class Group(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val recentActivity: List<String> = emptyList(),
    val groupBalance: Map<String, Double> = emptyMap()
)
