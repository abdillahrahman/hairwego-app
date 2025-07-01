package com.app.hairwego.data.repository


import android.content.Context
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.RegisterRequest
import com.app.hairwego.data.model.RegisterResponse
import com.app.hairwego.data.remote.retrofit.ApiConfig
import retrofit2.Response

class RegisterRepository(private val context: Context, private val tokenManager: TokenManager) {
    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        val apiService = ApiConfig.getApiService(context, tokenManager)
        return apiService.registerUser(request)
    }
}
