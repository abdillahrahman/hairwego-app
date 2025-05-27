package com.app.hairwego.ui.screen.register

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.RegisterRequest
import com.app.hairwego.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class RegisterViewModel(val context: Context, val tokenManager: TokenManager) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun registerUser(username: String, email: String, password: String, confirmPassword: String, context: Context) {
        val usernameValid = username.isNotEmpty()
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordValid = password.length >= 6
        val confirmPasswordValid = password == confirmPassword

        _uiState.value = RegisterUiState(
            usernameError = if (!usernameValid) "Username tidak boleh kosong" else null,
            emailError = if (!emailValid) "Email tidak valid" else null,
            passwordError = if (!passwordValid) "Password minimal 6 karakter" else null,
            confirmPasswordError = if (!confirmPasswordValid) "Password tidak cocok" else null
        )

        if (usernameValid && emailValid && passwordValid && confirmPasswordValid) {
            viewModelScope.launch {
                try {
                    val response = ApiConfig.getApiService(context,tokenManager ).registerUser(
                        RegisterRequest(username, email, password)
                    )
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        _uiState.value = _uiState.value.copy(emailError = "Email already exists")
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(emailError = "Error: ${e.message}")
                }
            }
        }
    }
}
