package com.app.hairwego.data.repository

import android.content.Context
import com.app.hairwego.data.local.FaceScanWithRecommendations
import com.app.hairwego.data.local.HairWeGoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class HistoryDetailRepository(context: Context) {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val dao = HairWeGoDatabase.getDatabase(context, applicationScope).historyDao()

    suspend fun getDetail(faceScanId: String): FaceScanWithRecommendations? {
        return dao.getHistoryDetail(faceScanId)
    }
}