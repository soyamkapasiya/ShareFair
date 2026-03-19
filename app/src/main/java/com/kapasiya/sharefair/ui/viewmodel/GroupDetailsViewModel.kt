package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Bill
import com.kapasiya.sharefair.model.User
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
                }
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
}

sealed class BillsUiState {
    object Loading : BillsUiState()
    data class Success(val bills: List<Bill>) : BillsUiState()
    data class Error(val message: String) : BillsUiState()
}
