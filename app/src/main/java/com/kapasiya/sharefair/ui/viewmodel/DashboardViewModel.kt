package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import com.kapasiya.sharefair.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardStats(
    val userName: String = "User",
    val profileImageUrl: String = "",
    val totalBalance: Double = 0.0,
    val youOwe: Double = 0.0,
    val owesYou: Double = 0.0,
    val spendingHistory: List<Pair<String, Double>> = emptyList(),
    val recentTransactions: List<Bill> = emptyList(),
    val friends: List<User> = emptyList()
)

class DashboardViewModel : ViewModel() {
    private val billRepository = RepositoryModule.billRepository
    private val userRepository = RepositoryModule.userRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        observeUserData()
        loadHistory()
    }

    private fun observeUserData() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId).collect { user ->
                user?.let { u ->
                    _stats.update { it.copy(
                        userName = u.name,
                        profileImageUrl = u.profileImageUrl,
                        totalBalance = u.totalBalance,
                        owesYou = if (u.totalBalance > 0) u.totalBalance else 0.0,
                        youOwe = if (u.totalBalance < 0) -u.totalBalance else 0.0
                    ) }
                    
                    // Also fetch friends
                    if (u.friends.isNotEmpty()) {
                        userRepository.getFriends(u.friends).collect { friendsList ->
                            _stats.update { it.copy(friends = friendsList) }
                        }
                    }
                }
            }
        }
    }

    private fun loadHistory() {
        // Placeholder stats - in a real app, logic would go here
        val history = listOf(
            "Mon" to 120.0, "Tue" to 450.0, "Wed" to 200.0, 
            "Thu" to 800.0, "Fri" to 300.0, "Sat" to 600.0, "Sun" to 150.0
        )
        _stats.update { it.copy(spendingHistory = history) }
    }
}
