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
            val personalBillsFlow = billRepository.getSoloBillsFlow(currentUserId)
            val groupsFlow = groupRepository.getGroupsForUserFlow(currentUserId)
            
            groupsFlow.flatMapLatest { groups ->
                if (groups.isEmpty()) {
                    personalBillsFlow.map { it }
                } else {
                    combine(groups.map { billRepository.getBillsForGroupFlow(it.id) }) { lists ->
                        lists.flatMap { it }
                    }.combine(personalBillsFlow) { groupBills, soloBills ->
                        groupBills + soloBills
                    }
                }
            }.collect { allBills ->
                processBills(allBills)
            }
        }
    }

    private fun processBills(bills: List<Bill>) {
        val relevantBills = bills.filter { bill ->
            bill.payerId == currentUserId || bill.participantsMap.containsKey(currentUserId)
        }

        val totalSpent = relevantBills.sumOf { it.amount }

        // Category breakdown
        val categoryData = relevantBills.groupBy { it.category }.map { entry ->
            CategorySpend(entry.key, entry.value.sumOf { it.amount }, 0)
        }.sortedByDescending { it.amount }

        // Monthly data (last 6 months)
        val dateFormat = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
        val monthlyData = relevantBills.groupBy { 
            dateFormat.format(java.util.Date(it.timestamp)) 
        }.map { entry ->
            MonthlySpend(entry.key, entry.value.sumOf { it.amount })
        }.takeLast(6)

        _uiState.value = SummaryUiState.Success(categoryData, monthlyData, totalSpent)
    }
}
