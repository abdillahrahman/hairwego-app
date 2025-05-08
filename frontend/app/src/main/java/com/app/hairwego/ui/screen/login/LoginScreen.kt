package com.app.hairwego.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
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

    uiState.errorMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }

    HairWeGoScaffold(modifier = Modifier) { paddingValues ->
        AuthBackgroundLogin()
        LoginScreenContent(
            modifier = Modifier,
            emailValue = email,
            onEmailValueChange = { email = it },
            emailFieldError = uiState.emailError != null,
            emailFieldLabel = "Email",
            emailErrorText = uiState.emailError, // NEW

            passwordValue = password,
            onPasswordValueChange = { password = it },
            passwordFieldError = uiState.passwordError != null,
            passwordFieldLabel = "Password",
            passwordErrorText = uiState.passwordError, // NEW
            onLoginClicked = { viewModel.login(email, password, rememberMe) },
            onRegisterClick = { navController.navigate("register") },
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
                    .padding(start = 16.dp)
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
    }
}


