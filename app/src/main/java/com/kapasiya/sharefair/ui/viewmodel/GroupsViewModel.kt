package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.Group
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupsViewModel : ViewModel() {
    private val groupRepository = RepositoryModule.groupRepository
    private val userRepository = RepositoryModule.userRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    private val _collections = MutableStateFlow<List<Group>>(emptyList())
    val collections: StateFlow<List<Group>> = _collections.asStateFlow()

    private val _activeGroups = MutableStateFlow<List<Group>>(emptyList())
    val activeGroups: StateFlow<List<Group>> = _activeGroups.asStateFlow()

    private val _currentUser = MutableStateFlow<com.kapasiya.sharefair.model.User?>(null)
    val currentUser: StateFlow<com.kapasiya.sharefair.model.User?> = _currentUser.asStateFlow()

    init {
        loadCurrentUser()
        loadGroups()
    }

    private fun loadCurrentUser() {
        if (currentUserId.isEmpty()) return
        
        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId).collect {
                _currentUser.value = it
            }
        }
    }

    private fun loadGroups() {
        if (currentUserId.isEmpty()) {
            _uiState.value = GroupsUiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            groupRepository.getGroupsForUserFlow(currentUserId)
                .catch { e ->
                    _uiState.value = GroupsUiState.Error(e.message ?: "Unknown error")
                }
                .collect { groups ->
                    _uiState.value = GroupsUiState.Success(groups)
                    
                    // Filter groups into collections and standard groups
                    _collections.value = groups.filter { it.category != "OTHERS" }
                    _activeGroups.value = groups.filter { it.category == "OTHERS" }
                }
        }
    }

    fun createGroup(
        name: String, 
        members: List<String>, 
        type: String = "GROUP",
        category: String = "OTHERS",
        simplifyBalances: Boolean = true,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: return
        val currentGroupsCount = (uiState.value as? GroupsUiState.Success)?.groups?.size ?: 0
        
        if (!user.isPremium && currentGroupsCount >= user.groupLimit) {
            onResult(false, "Free limit reached. Upgrade to Premium for unlimited groups.")
            return
        }

        viewModelScope.launch {
            try {
                // For PERSONAL groups, only the current user is a member
                val allMembers = if (type == "PERSONAL") {
                    listOf(currentUserId)
                } else {
                    (members + currentUserId).distinct()
                }

                val group = Group(
                    name = name,
                    members = allMembers,
                    type = type,
                    category = category,
                    simplifyBalances = simplifyBalances,
                    recentActivity = listOf("Group created by ${user.name}")
                )
                groupRepository.createGroup(group)
                onResult(true, "Group created successfully")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to create group")
            }
        }
    }

    fun importFromSplitwise(onResult: (Boolean, String) -> Unit) {
        _uiState.value = GroupsUiState.Loading
        viewModelScope.launch {
            try {
                // Simulate OAuth and Fetching from Splitwise API
                kotlinx.coroutines.delay(2500)
                
                // In a real implementation, we would iterate through Splitwise groups
                val importedGroup = Group(
                   name = "Splitwise: Apartment",
                   members = listOf(currentUserId),
                   recentActivity = listOf("Imported from Splitwise")
                )
                groupRepository.createGroup(importedGroup)
                
                onResult(true, "Successfully imported your Splitwise data!")
                loadGroups() // Refresh
            } catch (e: Exception) {
                onResult(false, "Splitwise import failed: ${e.message}")
                loadGroups()
            }
        }
    }
}

sealed class GroupsUiState {
    object Idle : GroupsUiState()
    object Loading : GroupsUiState()
    data class Success(val groups: List<Group>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}
