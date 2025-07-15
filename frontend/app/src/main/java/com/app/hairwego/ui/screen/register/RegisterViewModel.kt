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
import org.json.JSONObject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val fullnameError: String? = null,
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val successMessage: String? = null
)

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun registerUser(
        fullname: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        val fullnameValid = fullname.isNotEmpty()
        val usernameValid = username.isNotEmpty()
        val emailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val passwordValid = password.length >= 8
        val confirmPasswordValid = password == confirmPassword

        _uiState.value = RegisterUiState(
            fullnameError = if (!fullnameValid) "Full name must not be empty" else null,
            usernameError = if (!usernameValid) "Username must not be empty" else null,
            emailError = if (!emailValid) "Invalid email address" else null,
            passwordError = if (!passwordValid) "Password must be at least 8 characters" else null,
            confirmPasswordError = if (!confirmPasswordValid) "Passwords do not match" else null
        )

        if (fullnameValid && usernameValid && emailValid && passwordValid && confirmPasswordValid) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                try {
                    val response = repository.registerUser(
                        RegisterRequest(
                            fullname,
                            username,
                            email,
                            password
                        )
                    )
                    if (response.isSuccessful) {
                        _uiState.value = RegisterUiState(
                            isLoading = false,
                            successMessage = "Registration successful!"
                        )
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            "An error occurred"
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            usernameError = if (errorMessage.contains(
                                    "Username",
                                    ignoreCase = true
                                )
                            ) errorMessage else null,
                            emailError = if (errorMessage.contains(
                                    "Email",
                                    ignoreCase = true
                                )
                            ) errorMessage else null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        emailError = "An error occurred: ${e.message}"
                    )
                }
            }
        }
    }

    fun onFullnameChanged(fullname: String) {
        _uiState.update {
            it.copy(
                fullnameError = if (fullname.isNotEmpty()) null else "Full name must not be empty"
            )
        }
    }

    fun onUsernameChanged(username: String) {
        _uiState.update {
            it.copy(
                usernameError = if (username.isNotEmpty()) null else "Username must not be empty"
            )
        }
    }

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                emailError = if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) {
                    "Invalid email address"
                } else null
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                passwordError = if (password.isNotEmpty() && password.length < 8) {
                    "Password must be at least 8 characters"
                } else null
            )
        }
    }

    fun onConfirmPasswordChanged(password: String, confirmPassword: String) {
        _uiState.update {
            it.copy(
                confirmPasswordError = if (confirmPassword.isNotEmpty() && confirmPassword != password)
                    "Passwords do not match"
                else null
            )
        }
    }
}
