package com.app.hairwego.data.repository

import android.content.Context
import com.app.hairwego.data.local.HistoryDao
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.ProfileResponse
import com.app.hairwego.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileRepository(private val context: Context, private val tokenManager: TokenManager) {

    private val apiService = ApiConfig.getApiService(context, tokenManager)

    suspend fun getProfile(): ProfileResponse? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getProfile()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        }
    }

    suspend fun logout(dao: HistoryDao) {
        tokenManager.clearAllTokens()
        HistoryRepository(context, tokenManager).clearLocalHistory(dao)
    }
}