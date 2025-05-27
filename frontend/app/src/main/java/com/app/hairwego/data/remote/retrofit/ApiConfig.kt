package com.app.hairwego.data.remote.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import com.app.hairwego.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor

object ApiConfig {
    fun getApiService(context: Context, tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val tempRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.229:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tempApiService = tempRetrofit.create(ApiService::class.java)

        val client = OkHttpClient.Builder()
            .authenticator(TokenAuthenticator(tokenManager, tempApiService)) // ← Aman sekarang
            .addInterceptor { chain ->
                val token = runBlocking { tokenManager.getToken() } // karena suspend
                val request = chain.request().newBuilder()
                    .apply {
                        if (!token.isNullOrBlank()) {
                            addHeader("Authorization", "Bearer $token")
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.229:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}