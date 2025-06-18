package com.app.hairwego.data.remote.retrofit

import android.content.Context
import com.app.hairwego.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    fun getApiService(context: Context, tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val tempRetrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.229:5000/")
            /*.baseUrl("http://192.168.1.2:5000/")*/
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tempApiService = tempRetrofit.create(ApiService::class.java)

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager, tempApiService))
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.2.229:5000/")
            /*.baseUrl("http://192.168.1.2:5000/")*/
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
