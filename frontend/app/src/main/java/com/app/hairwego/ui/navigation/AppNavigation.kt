package com.app.hairwego.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.hairwego.ui.screen.home.HomeScreen
import com.app.hairwego.ui.screen.login.LoginScreen
import com.app.hairwego.ui.screen.register.RegisterScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("register") { RegisterScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen() }
    }
}