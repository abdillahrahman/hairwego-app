package com.app.hairwego

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.ui.navigation.NavigationItem
import com.app.hairwego.ui.navigation.Screen
import com.app.hairwego.ui.screen.History.HistoryScreen
import com.app.hairwego.ui.screen.HistoryDetail.HistoryDetailScreen
import com.app.hairwego.ui.screen.Splash.SplashScreen
import com.app.hairwego.ui.screen.home.HomeScreen
import com.app.hairwego.ui.screen.login.LoginScreen
import com.app.hairwego.ui.screen.profile.ProfileScreen
import com.app.hairwego.ui.screen.register.RegisterScreen
import com.app.hairwego.ui.theme.AppThemeViewModel
import kotlinx.coroutines.delay


@Composable
fun HairWeGoApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val viewModel: AppThemeViewModel = viewModel(factory = ViewModelFactory(context))
    val tokenManager = remember { TokenManager(context) }
    val startDestination = rememberSaveable { mutableStateOf<String?>(null) }

    val isDarkMode by viewModel.isDarkMode.collectAsState()

    HairwegoAppTheme(darkTheme = isDarkMode) {
        LaunchedEffect(Unit) {
            delay(3000)
            val token = tokenManager.getToken()
            val isGuest = tokenManager.isGuest()

            startDestination.value = when {
                !token.isNullOrEmpty() -> Screen.Home.route
                isGuest -> Screen.Home.route
                else -> Screen.Login.route
            }

            navController.navigate(startDestination.value!!) {
                popUpTo(0) { inclusive = true }
            }

        }

        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            bottomBar = {
                if (
                    currentRoute != Screen.Login.route &&
                    currentRoute != Screen.Register.route &&
                    currentRoute != Screen.Splash.route &&
                    currentRoute?.startsWith("history_detail") != true
                ) {
                    BottomBar(navController)
                }
            },
            modifier = modifier
        )

        { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Splash.route) {
                    SplashScreen()
                }
                composable(Screen.Login.route) {
                    LoginScreen(navController)
                }
                composable(Screen.Register.route) {
                    RegisterScreen(navController)
                }
                composable(Screen.Home.route) {
                    HomeScreen()
                }
                composable(Screen.History.route) {
                    HistoryScreen(navController)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(navController)
                }
                composable(
                    route = Screen.HistoryDetail.route,
                    arguments = listOf(navArgument("faceScanId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val faceScanId = backStackEntry.arguments?.getString("faceScanId") ?: ""
                    HistoryDetailScreen(faceScanId)
                }
            }
        }
    }
}


@Composable
private fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        NavigationItem(
            title = stringResource(R.string.menu_home),
            icon = Icons.Default.Home,
            screen = Screen.Home
        ),
        NavigationItem(
            title = stringResource(R.string.menu_history),
            icon = Icons.Default.CheckCircle,
            screen = Screen.History
        ),
        NavigationItem(
            title = stringResource(R.string.menu_profile),
            icon = Icons.Default.Person,
            screen = Screen.Profile
        )
    )

    NavigationBar(modifier = modifier) {
        navigationItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    if (currentRoute != item.screen.route) {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}
