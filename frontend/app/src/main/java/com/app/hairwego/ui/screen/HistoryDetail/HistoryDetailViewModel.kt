package com.app.hairwego.ui.screen.HistoryDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.hairwego.data.local.FaceScanWithRecommendations
import com.app.hairwego.data.repository.HistoryDetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryDetailViewModel(context: Context) : ViewModel() {
    private val repository = HistoryDetailRepository(context)

    private val _detail = MutableStateFlow<FaceScanWithRecommendations?>(null)
    val detail: StateFlow<FaceScanWithRecommendations?> get() = _detail

    fun loadDetail(faceScanId: String) {
        viewModelScope.launch {
            _detail.value = repository.getDetail(faceScanId)
        }
    }
}