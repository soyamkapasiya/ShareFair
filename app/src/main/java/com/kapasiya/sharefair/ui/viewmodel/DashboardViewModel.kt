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
                    
                    // Fetch friends
                    if (u.friends.isNotEmpty()) {
                        userRepository.getFriends(u.friends).collect { friendsList ->
                            _stats.update { it.copy(friends = friendsList) }
                        }
                    }
                    
                    // Fetch real transactions (Group + Solo)
                    val groupBillsFlows = u.groups.map { billRepository.getBillsForGroupFlow(it) }
                    val soloBillsFlow = billRepository.getSoloBillsFlow(currentUserId)
                    
                    if (groupBillsFlows.isEmpty()) {
                        soloBillsFlow.collect { soloBills ->
                            updateStatsFromBills(soloBills)
                        }
                    } else {
                        combine(groupBillsFlows) { lists -> lists.flatMap { it } }
                            .combine(soloBillsFlow) { groupBills, soloBills -> groupBills + soloBills }
                            .collect { allBills ->
                                updateStatsFromBills(allBills)
                            }
                    }
                }
            }
        }
    }

    private fun updateStatsFromBills(allBills: List<Bill>) {
        val sorted = allBills.sortedByDescending { it.timestamp }
        val recent = sorted.take(5)
        
        // Calculate history for last 7 days
        val calendar = java.util.Calendar.getInstance()
        val history = mutableListOf<Pair<String, Double>>()
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        
        for (i in 6 downTo 0) {
            val dayCalendar = java.util.Calendar.getInstance().apply { add(java.util.Calendar.DAY_OF_YEAR, -i) }
            val dayStart = dayCalendar.apply { set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0); set(java.util.Calendar.SECOND, 0) }.timeInMillis
            val dayEnd = dayCalendar.apply { set(java.util.Calendar.HOUR_OF_DAY, 23); set(java.util.Calendar.MINUTE, 59); set(java.util.Calendar.SECOND, 59) }.timeInMillis
            
            val dailySum = allBills.filter { it.timestamp in dayStart..dayEnd }.sumOf { it.amount }
            history.add(dayNames[dayCalendar.get(java.util.Calendar.DAY_OF_WEEK) - 1] to dailySum)
        }
        
        _stats.update { it.copy(recentTransactions = recent, spendingHistory = history) }
    }
}
