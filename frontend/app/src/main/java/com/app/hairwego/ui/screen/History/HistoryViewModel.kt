package com.app.hairwego.ui.screen.History

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.app.hairwego.data.local.FaceScanWithRecommendations
import com.app.hairwego.data.local.HairWeGoDatabase
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.repository.HistoryRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HistoryViewModel(
    context: Context,
    tokenManager: TokenManager
) : ViewModel() {

    private val dao = HairWeGoDatabase.getDatabase(context, viewModelScope).historyDao()
    private val repository = HistoryRepository(context, tokenManager)

    val history: LiveData<List<FaceScanWithRecommendations>> = dao.getAllHistory().asLiveData()

    var isFetching = mutableStateOf(true)
        private set

    var fetchError = mutableStateOf<String?>(null)
        private set

    fun fetchHistoryIfNeeded() {
        viewModelScope.launch {
            if (!repository.isHistoryFetched()) {
                try {
                    isFetching.value = true
                    repository.fetchAndSaveHistory(dao)
                    repository.setHistoryFetched(true)
                    fetchError.value = null
                } catch (e: Exception) {
                    fetchError.value = e.localizedMessage ?: "Gagal memuat data dari server"
                } finally {
                    isFetching.value = false
                }
            } else {
                isFetching.value = false
            }
        }
    }

    fun deleteHistory(id: String) {
        viewModelScope.launch {
            val success = repository.deleteHistoryById(id, dao)
            if (!success) {
                fetchError.value = "Failed to delete history"
            }
        }
    }

}
