package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class BillUiState {
    object Idle : BillUiState()
    object Loading : BillUiState()
    object Success : BillUiState()
    data class Error(val message: String) : BillUiState()
}

class BillViewModel : ViewModel() {
    private val billRepository = RepositoryModule.billRepository
    private val groupRepository = RepositoryModule.groupRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow<BillUiState>(BillUiState.Idle)
    val uiState: StateFlow<BillUiState> = _uiState.asStateFlow()

    private val _allBills = MutableStateFlow<List<Bill>>(emptyList())
    val allBills: StateFlow<List<Bill>> = _allBills.asStateFlow()

    init {
        loadAllUserBills()
    }

    private fun loadAllUserBills() {
        if (currentUserId.isEmpty()) return

        viewModelScope.launch {
            val groupBillsFlow = groupRepository.getGroupsForUserFlow(currentUserId).flatMapLatest { groups ->
                if (groups.isEmpty()) flowOf(emptyList<Bill>())
                else {
                    combine(groups.map { billRepository.getBillsForGroupFlow(it.id) }) { lists ->
                        lists.flatMap { it }
                    }
                }
            }
            
            val soloBillsFlow = billRepository.getSoloBillsFlow(currentUserId)
            
            combine(groupBillsFlow, soloBillsFlow) { groupBills, soloBills ->
                (groupBills + soloBills).sortedByDescending { it.timestamp }
            }.collect { merged ->
                _allBills.value = merged
            }
        }
    }

    fun addBill(
        groupId: String,
        title: String,
        amount: Double,
        splitType: String,
        participants: Map<String, Double>,
        items: List<com.kapasiya.sharefair.model.BillItem> = emptyList(),
        onSuccess: () -> Unit
    ) {
        if (currentUserId.isEmpty()) return

        _uiState.value = BillUiState.Loading
        viewModelScope.launch {
            try {
                val bill = Bill(
                    title = title,
                    amount = amount,
                    payerId = currentUserId,
                    splitType = splitType,
                    participantsMap = participants,
                    items = items,
                    timestamp = System.currentTimeMillis()
                )
                if (groupId == "solo") {
                    billRepository.addSoloBill(currentUserId, bill)
                } else {
                    billRepository.addBill(groupId, bill)
                }
                _uiState.value = BillUiState.Success
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Failed to add bill")
            }
        }
    }

    fun settleUp(groupId: String, friendId: String, amount: Double, onSuccess: () -> Unit) {
        if (currentUserId.isEmpty()) return
        
        _uiState.value = BillUiState.Loading
        viewModelScope.launch {
            try {
                billRepository.settleUp(groupId, currentUserId, friendId, amount)
                _uiState.value = BillUiState.Success
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = BillUiState.Error(e.message ?: "Failed to settle up")
            }
        }
    }
}
