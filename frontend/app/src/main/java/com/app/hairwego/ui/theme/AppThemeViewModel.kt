package com.app.hairwego.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val appPreferences = AppPreferences(application)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    init {
        viewModelScope.launch {
            appPreferences.isDarkModeEnabled.collect {
                _isDarkMode.value = it
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setDarkMode(enabled)
        }
    }
}
