package com.app.hairwego.ui.screen.login

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.data.local.HairWeGoDatabase
import com.app.hairwego.data.repository.HistoryRepository
import com.app.hairwego.data.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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

class LoginViewModel(
    context: Context,
    private val loginRepository: LoginRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { HairWeGoDatabase.getDatabase(context , applicationScope) }
    val dao = database.historyDao()

    fun login(email: String, password: String, rememberMe: Boolean) {
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordValid = password.length >= 8

        _uiState.update {
            it.copy(
                emailError = null,
                passwordError = null,
                errorMessage = null
            )
        }

        if (!emailValid || !passwordValid) {
            _uiState.update {
                it.copy(
                    emailError = if (!emailValid) "Email tidak valid" else null,
                    passwordError = if (!passwordValid) "Minimal 8 karakter" else null,
                    errorMessage = "Email atau password tidak valid"
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginRepository.login(email, password)
            result.onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, isLoginSuccess = true)
                }
                historyRepository.clearLocalHistory(dao)
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Login gagal: ${e.message}")
                }
            }
        }
    }


    fun setGuestMode(isGuest: Boolean) {
        viewModelScope.launch {
            loginRepository.setGuestMode(isGuest)
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                emailError = if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    "Email not valid"
                } else null
            )
        }
    }


    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                passwordError = if (password.isNotEmpty() && password.length < 8) {
                    "Minimal 8 karakter"
                } else null
            )
        }
    }

}
