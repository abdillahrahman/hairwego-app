package com.app.hairwego.data.remote.retrofit

import com.app.hairwego.data.model.LoginRequest
import com.app.hairwego.data.model.LoginResponse
import com.app.hairwego.data.model.PredictResponse
import com.app.hairwego.data.model.RegisterRequest
import com.app.hairwego.data.model.RegisterResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>


    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @Multipart
    @POST("/api/predict")
    suspend fun predict(
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): PredictResponse

    @POST("/auth/refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<LoginResponse>
}