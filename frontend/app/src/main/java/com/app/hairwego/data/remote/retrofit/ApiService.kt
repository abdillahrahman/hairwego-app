package com.app.hairwego.data.remote.retrofit

import com.app.hairwego.data.model.LoginRequest
import com.app.hairwego.data.model.LoginResponse
import com.app.hairwego.data.model.RegisterRequest
import com.app.hairwego.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    // ApiService.kt
        @POST("/auth/login")
        suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

}