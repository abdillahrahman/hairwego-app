package com.app.hairwego.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import com.app.hairwego.R
import com.app.hairwego.ViewModelFactory
import com.app.hairwego.data.local.HairWeGoDatabase
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

    // Tambahkan token manager dan pengecekan guest
    val tokenManager = remember { com.app.hairwego.data.local.TokenManager(context) }
    val isGuestState = remember { mutableStateOf(false) }

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { HairWeGoDatabase.getDatabase(context, applicationScope) }
    val dao = database.historyDao()


    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
        isGuestState.value = tokenManager.isGuest()
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
        // Tampilan untuk pengguna yang login
        ProfileContent(
            uiState = uiState,
            isDarkMode = isDarkMode,
            onToggleDarkMode = { appViewModel.toggleDarkMode(it) },
            onLogout = { viewModel.logout(navController, dao) },
            onEditProfile = { /* TODO: Navigate to edit profile screen */ }
        )
    }
}



@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
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
                    text = uiState.profile?.username ?: "Loading...",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text("Profile Information", style = MaterialTheme.typography.titleMedium)
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
                Text(text="Dark Mode", style = MaterialTheme.typography.bodyLarge)
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
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text="Logout", style = MaterialTheme.typography.bodyLarge)
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
            onEditProfile = {}
        )
    }
}



