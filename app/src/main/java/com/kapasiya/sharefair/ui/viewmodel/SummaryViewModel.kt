package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategorySpend(val category: String, val amount: Double, val color: Int)
data class MonthlySpend(val month: String, val amount: Double)

sealed class SummaryUiState {
    object Loading : SummaryUiState()
    data class Success(
        val categorySpends: List<CategorySpend>,
        val monthlySpends: List<MonthlySpend>,
        val totalSpent: Double
    ) : SummaryUiState()
    data class Error(val message: String) : SummaryUiState()
}

class SummaryViewModel : ViewModel() {
    private val billRepository = RepositoryModule.billRepository
    private val groupRepository = RepositoryModule.groupRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Loading)
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        loadSummaryData()
    }

    private fun loadSummaryData() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            groupRepository.getGroupsForUserFlow(currentUserId).collect { groups ->
                val allBillsFlows = groups.map { billRepository.getBillsForGroupFlow(it.id) }
                
                if (allBillsFlows.isEmpty()) {
                    _uiState.value = SummaryUiState.Success(emptyList(), emptyList(), 0.0)
                    return@collect
                }

                combine(allBillsFlows) { lists ->
                    lists.flatMap { it }
                }.collect { allBills ->
                    processBills(allBills)
                }
            }
        }
    }

    private fun processBills(bills: List<Bill>) {
        // Only consider bills where current user is a participant or payer
        val relevantBills = bills.filter { bill ->
            bill.payerId == currentUserId || bill.participantsMap.containsKey(currentUserId)
        }

        // Calculate what I actually "OWE" or "SPENT"
        // For simplicity, let's treat "Spent" as my share in each bill plus what I paid as a gift?
        // No, let's just use my share in each bill as my spending.
        
        val mySpends = relevantBills.map { it.participantsMap[currentUserId] ?: 0.0 }
        val totalSpent = mySpends.sum()

        // Mock categories for now as Bill model doesn't have categories yet
        // In a real app, bill would have a category field.
        val categories = listOf("Food", "Rent", "Travel", "Shopping", "Others")
        val categoryData = categories.mapIndexed { index, cat ->
            // Randomly distribute for demo
            CategorySpend(cat, totalSpent * (0.1 + (0.2 * (index % 3))), index)
        }

        // Mock monthly data
        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
        val monthlyData = months.map { MonthlySpend(it, totalSpent / 6 * (0.8 + Math.random() * 0.4)) }

        _uiState.value = SummaryUiState.Success(categoryData, monthlyData, totalSpent)
    }
}
