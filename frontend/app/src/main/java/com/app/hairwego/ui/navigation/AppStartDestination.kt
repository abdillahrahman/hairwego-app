package com.app.hairwego.ui.navigation

sealed class AppStartDestination {
    object Loading : AppStartDestination()
    object Login : AppStartDestination()
    object Home : AppStartDestination()
}