package com.app.hairwego.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.app.hairwego.data.local.HistoryDao
import com.app.hairwego.data.model.ProfileResponse
import com.app.hairwego.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: ProfileResponse? = null,
    val isDarkMode: Boolean = false
)

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun fetchProfile() {
        viewModelScope.launch {
            val profile = repository.getProfile()
            profile?.let {
                _uiState.update { it.copy(profile = profile) }
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
        // persist dark mode if needed
    }

    fun logout(navController: NavController, dao: HistoryDao) {
        viewModelScope.launch {
            repository.logout(dao)
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
