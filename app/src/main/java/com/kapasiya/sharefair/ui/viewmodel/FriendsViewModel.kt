package com.kapasiya.sharefair.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kapasiya.sharefair.data.RepositoryModule
import com.kapasiya.sharefair.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val userRepository = RepositoryModule.userRepository
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""

    private val _uiState = MutableStateFlow<FriendsUiState>(FriendsUiState.Loading)
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    private fun loadFriends() {
        if (currentUserId.isEmpty()) {
            _uiState.value = FriendsUiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId)
                .flatMapLatest { user ->
                    if (user == null) {
                        flowOf(emptyList<User>())
                    } else if (user.friends.isEmpty()) {
                        flowOf(emptyList<User>())
                    } else {
                        // For each friend ID, get their flow and combine them
                        // However, to keep it simple and real-time for now, we can just fetch them once or use a combined flow
                        // Best practice for Firestore: use 'whereIn' if the list is small (<30)
                        // Or better: individual flows combined
                        combine(user.friends.map { userRepository.getUserFlow(it) }) { users ->
                            users.filterNotNull()
                        }
                    }
                }
                .catch { e ->
                    _uiState.value = FriendsUiState.Error(e.message ?: "Unknown error")
                }
                .collect { friends ->
                    _uiState.value = FriendsUiState.Success(friends)
                }
        }
    }

    fun addFriend(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val friend = userRepository.findUserByEmail(email)
                if (friend == null) {
                    onResult(false, "User not found")
                    return@launch
                }
                if (friend.id == currentUserId) {
                    onResult(false, "You cannot add yourself")
                    return@launch
                }
                userRepository.addFriend(currentUserId, friend.id)
                onResult(true, "Friend added successfully")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Failed to add friend")
            }
        }
    }
}

sealed class FriendsUiState {
    object Loading : FriendsUiState()
    data class Success(val friends: List<User>) : FriendsUiState()
    data class Error(val message: String) : FriendsUiState()
}
