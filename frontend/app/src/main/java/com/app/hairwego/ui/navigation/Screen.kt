package com.app.hairwego.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
    data object History : Screen("history")
}
