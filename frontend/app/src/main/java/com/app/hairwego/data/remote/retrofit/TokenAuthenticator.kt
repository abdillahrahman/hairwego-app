package com.app.hairwego.data.remote.retrofit

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import kotlinx.coroutines.runBlocking
import com.app.hairwego.data.local.TokenManager

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val apiService: ApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val newToken = runBlocking {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken.isNullOrBlank()) return@runBlocking null

            try {
                val result = apiService.refreshToken(refreshToken)
                if (result.isSuccessful) {
                    val accessToken = result.body()?.access_token
                    if (!accessToken.isNullOrBlank()) {
                        tokenManager.saveToken(accessToken)
                        accessToken
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        } ?: return null

        return response.request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response.priorResponse
        while (res != null) {
            count++
            res = res.priorResponse
        }
        return count
    }
}
