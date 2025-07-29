package com.mobicom.s17.group8.mobicom_mco.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserAuthViewModel : ViewModel() {

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    // Function to set the current user ID
    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }

    // Function to clear the current user ID (e.g., on logout)
    fun clearCurrentUser() {
        _currentUserId.value = null
    }
}