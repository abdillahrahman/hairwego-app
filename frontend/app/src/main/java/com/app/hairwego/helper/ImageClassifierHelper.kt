
package com.app.hairwego.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import com.app.hairwego.R
import com.app.hairwego.data.model.PredictResponse
import com.app.hairwego.data.remote.retrofit.ApiConfig
import com.app.hairwego.reduceFileImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.app.hairwego.data.Result
import com.app.hairwego.data.local.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.core.graphics.scale

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener,
    private val tokenManager: TokenManager
) {

    fun classifyImage(imageUri: Uri) {
        val bitmap = uriToBitmap(imageUri)
        if (bitmap == null) {
            classifierListener.onError(context.getString(R.string.error_unable_to_load_image))
            return
        }

        val scaledBitmap = bitmap.scale(1024, (1024 * bitmap.height / bitmap.width))

        val imageFile = bitmapToFile(scaledBitmap, "temp_image.jpg")
        if (imageFile == null) {
            classifierListener.onError(context.getString(R.string.error_unable_to_process_image))
            return
        }

        val reducedFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageFile.reduceFileImage()
        } else {
            TODO("VERSION.SDK_INT < Q")
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = reducedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData(
                    "file",
                    reducedFile.name,
                    requestBody
                )

                Log.d("REDUCE_SIZE", "Before: ${imageFile.length()}, After: ${reducedFile.length()}")

                withContext(Dispatchers.Main) {
                    classifierListener.onResult(Result.Loading)
                }

                val token = getToken()
                Log.d("TOKEN_CHECK", "Token: $token")
                val apiService = ApiConfig.getApiService(context, tokenManager)
                val response = apiService.predict(multipartBody)
                Log.d("TOKEN_CHECK", "Token: $token")

                withContext(Dispatchers.Main) {
                    try {
                        classifierListener.onResult(Result.Success(response))
                    } catch (e: Exception) {
                        classifierListener.onResult(Result.Error("Error parsing response: ${e.message}"))
                    }
                }
            } catch (e: Exception) {
                val errorMessage = if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    try {
                        org.json.JSONObject(errorBody).getString("message")
                    } catch (jsonException: Exception) {
                        "Gagal memuat hasil prediksi. Kode: ${e.code()}"
                    }
                } else {
                    "Terjadi kesalahan jaringan atau server: ${e.message}"
                }

                withContext(Dispatchers.Main) {
                    classifierListener.onResult(Result.Error(errorMessage))
                }
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String = "temp_image.jpg"): File? {
        return try {
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun uriToBitmap(imageUri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(imageUri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("ImageClassifierHelper", "uriToBitmap: Failed to load image. ${e.message}")
            null
        }
    }

    interface ClassifierListener {
        fun onResult(result: Result<PredictResponse>)
        fun onError(error: String)
    }

    private suspend fun getToken(): String {
        return tokenManager.getToken() ?: ""
    }
}
