package com.app.hairwego.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.hairwego.R
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.repository.ProfileRepository
import com.app.hairwego.ui.theme.AppThemeViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    val appViewModel: AppThemeViewModel = viewModel(factory = ViewModelFactory(context))
    val isDarkMode by appViewModel.isDarkMode.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineMedium)

        if (uiState.profile != null) {
            Text("Username: ${uiState.profile!!.username}")
            Text("Email: ${uiState.profile!!.email}")
            Text("Total Scans: ${uiState.profile!!.totalScans}")
            Text("Latest Face Shape: ${uiState.profile!!.latestFaceShape}")
        } else {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_dark_mode_24), // vector
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dark Mode")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkMode,
                onCheckedChange = { appViewModel.toggleDarkMode(it) }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                viewModel.logout(navController)
            }
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout")
        }
    }
}