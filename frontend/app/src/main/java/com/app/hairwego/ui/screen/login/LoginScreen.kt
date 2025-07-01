package com.app.hairwego.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.ahmetocak.shoppingapp.presentation.designsystem.components.AuthEnterEmailOtf
import com.ahmetocak.shoppingapp.presentation.designsystem.components.AuthEnterPasswordOtf
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.ui.components.AuthBackgroundLogin
import com.app.hairwego.ui.components.HairWeGoScaffold
import com.app.hairwego.ui.components.MyButton
import com.app.hairwego.ui.components.WelcomeText
import com.app.hairwego.R

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = viewModel(factory = ViewModelFactory(context))

    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // ✅ Tampilkan snackbar jika errorMessage ada
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    HairWeGoScaffold(modifier = Modifier, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        AuthBackgroundLogin()
        // In LoginScreen
        LoginScreenContent(
            modifier = Modifier,
            emailValue = email,
            onEmailValueChange = {
                email = it
                viewModel.onEmailChanged(it)
            },
            emailFieldError = uiState.emailError != null,
            emailFieldLabel = "Email",
            emailErrorText = uiState.emailError,
            passwordValue = password,
            onPasswordValueChange = {
                password = it
                viewModel.onPasswordChanged(it)
            },
            passwordFieldError = uiState.passwordError != null,
            passwordFieldLabel = "Password",
            passwordErrorText = uiState.passwordError,
            onLoginClicked = { viewModel.login(email, password, rememberMe) },
            onRegisterClick = { navController.navigate("register") },
            onGuestLoginClick = {
                viewModel.setGuestMode(true)
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            },
            uiState = uiState
        )
    }
}


@Composable
private fun LoginScreenContent(
    modifier: Modifier,
    emailValue: String,
    passwordValue: String,
    onEmailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    emailFieldError: Boolean,
    emailFieldLabel: String,
    emailErrorText: String?, // NEW
    passwordFieldError: Boolean,
    passwordFieldLabel: String,
    passwordErrorText: String?, // NEW
    onLoginClicked: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestLoginClick: () -> Unit,
    uiState: LoginUiState

) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.two_level_margin))
            .padding(bottom = dimensionResource(id = R.dimen.eight_level_margin)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        WelcomeText(text = "Welcome")

        AuthEnterEmailOtf(
            value = emailValue,
            onValueChange = onEmailValueChange,
            isError = emailFieldError,
            labelText = emailFieldLabel
        )
        if (emailErrorText != null) {
            Text(
                text = emailErrorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            )
        }

        AuthEnterPasswordOtf(
            value = passwordValue,
            onValueChange = onPasswordValueChange,
            isError = passwordFieldError,
            labelText = passwordFieldLabel
        )
        if (passwordErrorText != null) {
            Text(
                text = passwordErrorText,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp)
            )
        }

        MyButton(buttonText = "Login", onClick = onLoginClicked)

        Row(
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.two_level_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.no_account))
            TextButton(onClick = onRegisterClick) {
                Text(text = stringResource(id = R.string.register_now))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("────────── or ──────────")

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onGuestLoginClick) {
            Text(text = "Login as Guest", fontSize = 16.sp)
        }



        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


