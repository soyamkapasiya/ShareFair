package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import com.kapasiya.sharefair.model.User
import com.kapasiya.sharefair.utils.BalanceSimplifier
import com.kapasiya.sharefair.utils.Transaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupDetailsViewModel(private val groupId: String) : ViewModel() {
    private val billRepository = RepositoryModule.billRepository
    private val groupRepository = RepositoryModule.groupRepository
    private val userRepository = RepositoryModule.userRepository

    private val _billsUiState = MutableStateFlow<BillsUiState>(BillsUiState.Loading)
    val billsUiState: StateFlow<BillsUiState> = _billsUiState.asStateFlow()

    val groupFlow = groupRepository.getGroupFlow(groupId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members: StateFlow<List<User>> = _members.asStateFlow()

    private val _netBalances = MutableStateFlow<Map<String, Double>>(emptyMap())
    val netBalances: StateFlow<Map<String, Double>> = _netBalances.asStateFlow()

    private val _simplifiedTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val simplifiedTransactions: StateFlow<List<Transaction>> = _simplifiedTransactions.asStateFlow()

    init {
        loadBills()
        observeMembers()
    }

    private fun loadBills() {
        if (groupId.isEmpty()) return

        viewModelScope.launch {
            billRepository.getBillsForGroupFlow(groupId)
                .catch { e ->
                    _billsUiState.value = BillsUiState.Error(e.message ?: "Unknown error")
                }
                .collect { bills ->
                    _billsUiState.value = BillsUiState.Success(bills)
                    calculateBalances(bills)
                }
        }
    }

    private fun calculateBalances(bills: List<Bill>) {
        val balances = mutableMapOf<String, Double>()
        
        bills.forEach { bill ->
            if (bill.splitType == "SETTLEMENT") {
                // Payer gave money to someone else
                val payerId = bill.payerId
                val amount = bill.amount
                // Receiver is the one in participantsMap
                val receiverId = bill.participantsMap.keys.firstOrNull()
                
                if (receiverId != null) {
                    balances[payerId] = (balances[payerId] ?: 0.0) + amount
                    balances[receiverId] = (balances[receiverId] ?: 0.0) - amount
                }
            } else {
                // Update payer
                val currentPayerBalance = balances[bill.payerId] ?: 0.0
                balances[bill.payerId] = currentPayerBalance + (bill.amount - (bill.participantsMap[bill.payerId] ?: 0.0))
                
                // Update participants
                bill.participantsMap.forEach { (userId, share) ->
                    if (userId != bill.payerId) {
                        val currentBalance = balances[userId] ?: 0.0
                        balances[userId] = currentBalance - share
                    }
                }
            }
        }
        
        _netBalances.value = balances
        
        // Only simplify if the group setting allows it
        if (groupFlow.value?.simplifyBalances != false) {
            _simplifiedTransactions.value = BalanceSimplifier.simplify(balances)
        } else {
            // If not simplified, we might want to show raw debts, 
            // but for now let's just use the same logic or empty
            _simplifiedTransactions.value = BalanceSimplifier.simplify(balances) 
        }
    }

    private fun observeMembers() {
        viewModelScope.launch {
            groupFlow.collect { group ->
                group?.members?.let { ids ->
                    if (ids.isNotEmpty()) {
                        userRepository.getFriends(ids).collect { userList ->
                            _members.value = userList
                        }
                    }
                }
            }
        }
    }
    
    fun settleUp(fromUserId: String, toUserId: String, amount: Double) {
        viewModelScope.launch {
            billRepository.settleUp(groupId, fromUserId, toUserId, amount)
        }
    }
}

sealed class BillsUiState {
    object Loading : BillsUiState()
    data class Success(val bills: List<Bill>) : BillsUiState()
    data class Error(val message: String) : BillsUiState()
}
