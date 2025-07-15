package com.app.hairwego.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.hairwego.R
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.HairWeGoDatabase
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.ui.navigation.Screen
import com.app.hairwego.ui.theme.AppThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(factory = ViewModelFactory(context))
    val appViewModel: AppThemeViewModel = viewModel(factory = ViewModelFactory(context))
    val isDarkMode by appViewModel.isDarkMode.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val showLogoutDialog = remember { mutableStateOf(false) }

    val tokenManager = remember { TokenManager(context) }
    val isGuestState = remember { mutableStateOf(false) }

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { HairWeGoDatabase.getDatabase(context, applicationScope) }
    val dao = database.historyDao()


    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
        isGuestState.value = tokenManager.isGuest()
    }

    if (showLogoutDialog.value) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog.value = false },
            title = { Text("Confirm Logout") },
            text = {
                Text(
                    "Are you sure you want to logout?",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout(navController, dao)
                    showLogoutDialog.value = false
                }) {
                    Text("Yes", style = MaterialTheme.typography.titleMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog.value = false
                }) {
                    Text("Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }
        )
    }

    if (isGuestState.value) {
        // Tampilan khusus Guest
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Login to view your profile and settings.",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate(Screen.Login.route)
                }) {
                    Text("Login")
                }
            }
        }
    } else {
        ProfileContent(
            uiState = uiState,
            isDarkMode = isDarkMode,
            onToggleDarkMode = { appViewModel.toggleDarkMode(it) },
            onAbout = { navController.navigate(Screen.History.route) },
            onLogout = { showLogoutDialog.value = true },
        )
    }
}


@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onAbout: () -> Unit = {},
    onLogout: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF5B34A3), Color(0xFF9F44D3))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.profile_placeholder),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(124.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(3.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.profile?.fullname ?: "Guest User",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Profile Information", style = MaterialTheme.typography.titleMedium)
            InfoRow(icon = Icons.Default.AccountCircle, label = uiState.profile?.fullname ?: "Name")
            InfoRow(icon = Icons.Default.Person, label = uiState.profile?.username ?: "Username")
            InfoRow(icon = Icons.Default.Email, label = uiState.profile?.email ?: "Email")

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )

            Text("Settings", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_dark_mode_24),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Dark Mode", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggleDarkMode
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onLogout() }
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", style = MaterialTheme.typography.bodyLarge)
            }
        }


    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    val dummyProfile = ProfileUiState(
        profile = com.app.hairwego.data.model.ProfileResponse(
            username = "Anna Avetisyan",
            email = "info@aplusdesign.co",
            totalScans = 25,
            latestFaceShape = "Oval"
        )
    )

    val isDarkMode = remember { mutableStateOf(false) }

    MaterialTheme {
        ProfileContent(
            uiState = dummyProfile,
            isDarkMode = isDarkMode.value,
            onToggleDarkMode = { isDarkMode.value = it },
            onLogout = {},
        )
    }
}



