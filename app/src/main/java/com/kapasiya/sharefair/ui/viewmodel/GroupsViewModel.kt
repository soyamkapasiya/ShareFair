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

    init {
        loadGroups()
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
                }
        }
    }

    fun createGroup(name: String, members: List<String>, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                // Ensure current user is in members list
                val allMembers = (members + currentUserId).distinct()
                val group = Group(
                    name = name,
                    members = allMembers,
                    recentActivity = listOf("Group created by current user")
                )
                groupRepository.createGroup(group)
                onResult(true, "Group created successfully")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to create group")
            }
        }
    }
}

sealed class GroupsUiState {
    object Loading : GroupsUiState()
    data class Success(val groups: List<Group>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}
