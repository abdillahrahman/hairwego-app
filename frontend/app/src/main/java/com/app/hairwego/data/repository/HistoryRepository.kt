package com.app.hairwego.data.repository

import android.content.Context
import com.app.hairwego.data.local.HistoryDao
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.remote.retrofit.ApiConfig
import com.app.hairwego.mapToEntities

class HistoryRepository (
    private val context: Context,
    private val tokenManager: TokenManager
) {
     suspend fun fetchAndSaveHistory(dao : HistoryDao) {
        val apiService = ApiConfig.getApiService(context, tokenManager)
        val response = apiService.getHistory()
        val (scans, recommendations) = mapToEntities(context, response)

        scans.forEach { dao.insertFaceScan(it) }
        dao.insertRecommendations(recommendations)
    }

    fun isHistoryFetched(): Boolean {
        val prefs = context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("isHistoryFetched", false)
    }

    fun setHistoryFetched(fetched: Boolean) {
        val prefs = context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("isHistoryFetched", fetched).apply()
    }
}