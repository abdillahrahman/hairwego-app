package com.app.hairwego.ui.screen.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.data.model.RegisterRequest
import com.app.hairwego.data.repository.RegisterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val successMessage: String? = null
)

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun registerUser(username: String, email: String, password: String, confirmPassword: String) {
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
                _uiState.value = _uiState.value.copy(isLoading = true)
                try {
                    val response = repository.registerUser(RegisterRequest(username, email, password))
                    if (response.isSuccessful) {
                        _uiState.value = RegisterUiState(
                            isLoading = false,
                            successMessage = "Registrasi berhasil!"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            emailError = "Email sudah digunakan"
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailError = "Terjadi kesalahan: ${e.message}"
                    )
                }
            }
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                emailError = if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    "Email tidak valid"
                } else null
            )
        }
    }


    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                passwordError = if (password.isNotEmpty() && password.length < 6) {
                    "Minimal 6 karakter"
                } else null
            )
        }
    }

    fun onConfirmPasswordChanged(password: String, confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != password)
                    "Password tidak cocok"
                else null
            )
        }
    }
}
