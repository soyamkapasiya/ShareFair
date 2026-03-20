package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PersonalUiState {
    object Loading : PersonalUiState()
    data class Success(
        val soloBills: List<Bill>,
        val totalSpent: Double,
        val categoryBreakdown: Map<String, Double>
    ) : PersonalUiState()
    data class Error(val message: String) : PersonalUiState()
}

class PersonalViewModel : ViewModel() {
    private val billRepository = RepositoryModule.billRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow<PersonalUiState>(PersonalUiState.Loading)
    val uiState: StateFlow<PersonalUiState> = _uiState.asStateFlow()

    init {
        loadPersonalData()
    }

    private fun loadPersonalData() {
        if (currentUserId.isEmpty()) {
            _uiState.value = PersonalUiState.Error("Not logged in")
            return
        }

        viewModelScope.launch {
            billRepository.getSoloBillsFlow(currentUserId)
                .catch { e -> _uiState.value = PersonalUiState.Error(e.message ?: "Error") }
                .collect { bills ->
                    val total = bills.sumOf { it.amount }
                    // Simplified category logic for now
                    val breakdown = mapOf(
                        "Food" to bills.filter { it.title.contains("food", true) || it.title.contains("eat", true) }.sumOf { it.amount },
                        "Travel" to bills.filter { it.title.contains("uber", true) || it.title.contains("travel", true) }.sumOf { it.amount },
                        "Misc" to (total - bills.filter { it.title.contains("food", true) || it.title.contains("uber", true) }.sumOf { it.amount })
                    )
                    
                    _uiState.value = PersonalUiState.Success(
                        soloBills = bills,
                        totalSpent = total,
                        categoryBreakdown = breakdown
                    )
                }
        }
    }
}
