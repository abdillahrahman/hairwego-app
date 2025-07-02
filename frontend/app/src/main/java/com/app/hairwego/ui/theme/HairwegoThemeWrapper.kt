package com.app.hairwego.ui.theme


import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.app.hairwego.ViewModelFactory

@Composable
fun HairwegoThemeWrapper(
    context: Context,
    content: @Composable () -> Unit
) {
    val appThemeViewModel: AppThemeViewModel = viewModel(factory = ViewModelFactory(context))
    val isDarkMode by appThemeViewModel.isDarkMode.collectAsState()

    HairwegoAppTheme(darkTheme = isDarkMode) {
        content()
    }
}
