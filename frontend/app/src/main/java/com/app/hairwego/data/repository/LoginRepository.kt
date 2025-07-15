package com.app.hairwego.data.repository


import android.content.Context
import com.app.hairwego.data.local.TokenManager
import com.app.hairwego.data.model.LoginRequest
import com.app.hairwego.data.remote.retrofit.ApiConfig
import org.json.JSONObject
import retrofit2.HttpException

class LoginRepository(
    private val context: Context,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = ApiConfig.getApiService(context, tokenManager)
                .login(LoginRequest(email, password))

            val accessToken = response.access_token
            val refreshToken = response.refresh_token

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()) {
                tokenManager.saveToken(accessToken)
                tokenManager.saveRefreshToken(refreshToken)
                tokenManager.setGuest(false)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Login Invalid"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                JSONObject(errorBody ?: "").getString("message")
            } catch (_: Exception) {
                "Login Failed"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setGuestMode(isGuest: Boolean) {
        tokenManager.setGuest(isGuest)
    }
}
