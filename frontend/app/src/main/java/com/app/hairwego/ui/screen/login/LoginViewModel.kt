package com.app.hairwego.ui.screen.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.LoginRequest
import com.app.hairwego.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String, rememberMe: Boolean) {
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordValid = password.length >= 6

        if (!emailValid || !passwordValid) {
            _uiState.update {
                it.copy(
                    emailError = if (!emailValid) "Email tidak valid" else null,
                    passwordError = if (!passwordValid) "Minimal 6 karakter" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = ApiConfig.getApiService().login(LoginRequest(email, password))
                val token = response.access_token

                if (token != null) {
                    tokenManager.saveToken(token)
                    if (rememberMe) {
                        tokenManager.setRememberMe(true)
                    }
                    _uiState.update {
                        it.copy(isLoading = false, isLoginSuccess = true)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Token tidak ditemukan")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Login gagal: ${e.message}")
                }
            }
        }
    }
}
