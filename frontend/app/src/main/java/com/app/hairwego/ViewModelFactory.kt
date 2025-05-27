package com.app.hairwego

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.ui.screen.login.LoginViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val tokenManager = TokenManager(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(context, tokenManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
