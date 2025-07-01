package com.app.hairwego

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.repository.LoginRepository
import com.app.hairwego.data.repository.ProfileRepository
import com.app.hairwego.data.repository.RegisterRepository
import com.app.hairwego.ui.screen.History.HistoryViewModel
import com.app.hairwego.ui.screen.HistoryDetail.HistoryDetailViewModel
import com.app.hairwego.ui.screen.login.LoginViewModel
import com.app.hairwego.ui.screen.profile.ProfileViewModel
import com.app.hairwego.ui.screen.register.RegisterViewModel
import com.app.hairwego.ui.theme.AppThemeViewModel

class ViewModelFactory(private val context: Context,
    ) : ViewModelProvider.Factory {

    private val tokenManager = TokenManager(context)
    private val loginRepository = LoginRepository(context, tokenManager)
    private val registerRepository = RegisterRepository(context, tokenManager)
    private val profileRepository = ProfileRepository(context, tokenManager)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(loginRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(registerRepository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(context, tokenManager) as T
            modelClass.isAssignableFrom(HistoryDetailViewModel::class.java) ->
                HistoryDetailViewModel(context) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(profileRepository) as T
            modelClass.isAssignableFrom(AppThemeViewModel::class.java) ->
                AppThemeViewModel(context.applicationContext as Application) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
