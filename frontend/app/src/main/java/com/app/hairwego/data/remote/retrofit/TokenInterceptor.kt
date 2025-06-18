package com.app.hairwego.data.remote.retrofit

import com.app.hairwego.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager,
    private val apiService: ApiService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val originalToken = runBlocking { tokenManager.getToken() }

        if (!originalToken.isNullOrBlank()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $originalToken")
                .build()
        }

        val response = chain.proceed(request)

        if (response.code == 401) {
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            if (errorBody.contains("Token has expired")) {
                val newToken = runBlocking {
                    val refresh = tokenManager.getRefreshToken()
                    if (!refresh.isNullOrBlank()) {
                        try {
                            val res = apiService.refreshToken("Bearer $refresh")
                            if (res.isSuccessful) {
                                val accessToken = res.body()?.access_token
                                if (!accessToken.isNullOrBlank()) {
                                    tokenManager.saveToken(accessToken)
                                    accessToken
                                } else null
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    } else null
                }

                if (!newToken.isNullOrBlank()) {
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    response.close()
                    return chain.proceed(newRequest)
                }
            }
        }

        return response
    }
}
