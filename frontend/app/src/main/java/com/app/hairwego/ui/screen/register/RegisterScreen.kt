package com.app.hairwego.ui.screen.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ahmetocak.shoppingapp.presentation.designsystem.components.AuthEnterEmailOtf
import com.ahmetocak.shoppingapp.presentation.designsystem.components.AuthEnterPasswordOtf
import com.app.hairwego.ui.components.AuthBackgroundRegister
import com.app.hairwego.ui.components.AuthEnterUsernameOtf
import com.app.hairwego.ui.components.HairWeGoScaffold
import com.app.hairwego.ui.components.MyButton
import com.app.hairwego.ui.components.WelcomeText
import com.app.hairwego.R

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = viewModel()) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    HairWeGoScaffold(modifier = Modifier) { paddingValues ->
        AuthBackgroundRegister()
        RegisterScreenContent(
            username = username,
            onUsernameChange = { username = it },
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = { confirmPassword = it },
            onRegisterClick = {
                viewModel.registerUser(username, email, password, confirmPassword, context) // Pass context here
            },
            onLoginClick = {
                navController.navigate("login")
            },
            uiState = uiState
        )
    }
}


@Composable
fun RegisterScreenContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    uiState: RegisterUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.two_level_margin))
            .padding(bottom = dimensionResource(id = R.dimen.eight_level_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        WelcomeText(text = "Register")

        AuthEnterUsernameOtf(
            value = username,
            onValueChange = onUsernameChange,
            isError = uiState.usernameError != null,
            labelText = "Username"
        )

        AuthEnterEmailOtf(
            value = email,
            onValueChange = onEmailChange,
            isError = uiState.emailError != null,
            labelText = "Email"
        )

        if (uiState.emailError != null) {
            Text(
                text = uiState.emailError ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            )
        }

        AuthEnterPasswordOtf(
            value = password,
            onValueChange = onPasswordChange,
            isError = uiState.passwordError != null,
            labelText = "Password"
        )

        if (uiState.passwordError != null) {
            Text(
                text = uiState.passwordError ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            )
        }

        AuthEnterPasswordOtf(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            isError = uiState.confirmPasswordError != null,
            labelText = "Confirm Password"
        )

        if (uiState.confirmPasswordError != null) {
            Text(
                text = uiState.confirmPasswordError ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            )
        }

        MyButton(buttonText = "Register", onClick = onRegisterClick)

        Row(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.two_level_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account?")
            TextButton(onClick = onLoginClick) {
                Text(text = "Login")
            }
        }
    }
}
